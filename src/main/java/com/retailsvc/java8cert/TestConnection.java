package com.retailsvc.java8cert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class TestConnection {

  /**
   * This will patch the cacerts trust store in the JRE to include the ISRG Root X1 certificate from
   * LetsEncrypt.
   *
   * @throws IOException if the patch fails
   * @throws InterruptedException if interrupted while waiting for the patch to complete
   */
  private static void patch() throws IOException, InterruptedException {
    Process keytool =
        new ProcessBuilder(
                "/usr/bin/keytool",
                "-importcert",
                "-noprompt",
                "-storepass",
                "changeit",
                "-alias",
                "isrgrootx1",
                "-keystore",
                "/usr/lib/jvm/zulu-8-amd64/jre/lib/security/cacerts",
                "-file",
                "/letsencrypt/isrgroot1.pem")
            .inheritIO()
            .redirectErrorStream(true)
            .start();

    int success = keytool.waitFor();
    if (success != 0) {
      throw new IOException("ISRG Root X1 patch failed");
    }
  }

  @SuppressWarnings("java:S106")
  public static void main(String[] args) throws Exception {
    if (Arrays.asList(args).contains("--patch")) {
      patch();
    }

    Map<String, Result> results = new TreeMap<>();
    for (String domain : args) {
      if (domain.equals("--patch")) {
        continue;
      }
      TestConnection conn = new TestConnection(domain);
      try {
        results.put(conn.domain, new Result(conn.verify()));
      } catch (IOException e) {
        results.put(conn.domain, new Result(e));
      }
    }

    int exit = 0;
    if (results.isEmpty()) {
      System.out.println("No domains specified.");
      System.out.println();
      System.out.println(
          "usage: java -jar java8-certificate-test.jar [--patch] [DOMAIN ...]");
      exit = 1;
    }

    for (Map.Entry<String, Result> entry : results.entrySet()) {
      System.out.println(entry.getKey() + "\n" + entry.getValue() + "\n");
      if (entry.getValue().status < 0) {
        exit = 1;
      }
    }

    System.out.println("\nResult: " + (exit == 1 ? "ERROR" : "SUCCESS"));
    System.exit(exit);
  }

  private final String domain;

  public TestConnection(String domain) {
    if (domain.startsWith("http://")) {
      throw new IllegalArgumentException(
          "http protocol is not supported. Use https:// or no protocol.");
    }
    if (!domain.startsWith("https://")) {
      this.domain = "https://" + domain;
    } else {
      this.domain = domain;
    }
  }

  public int verify() throws IOException {
    HttpURLConnection conn = (HttpURLConnection) URI.create(domain).toURL().openConnection();
    conn.setRequestMethod("OPTIONS");
    conn.connect();
    int response = conn.getResponseCode();
    conn.disconnect();
    return response;
  }

  private static class Result {
    private final int status;
    private final Throwable cause;

    public Result(int status) {
      this.status = status;
      this.cause = null;
    }

    public Result(Throwable cause) {
      this.status = -1;
      this.cause = cause;
    }

    @Override
    public String toString() {
      return status > 0
          ? "SUCCESS " + status
          : "ERROR: " + Optional.ofNullable(cause).map(Throwable::getMessage).orElse("unknown");
    }
  }
}
