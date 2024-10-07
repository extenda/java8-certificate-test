# java8-certificate-test

This is the repository for a Docker image that can be used to test LetsEncrypt compatibility on Java 8.

## Usage

This program can be run containerized or on a local Java distribution. 
Patching feature is only available on Docker.

### Docker

```bash
docker run --rm extenda/java8-certificate-test:8u102 \
  testrunner.hiiretail.com \
  letsencrypt.org
```
The command will exit with status 1 if it fails to connect to any of the domains.

#### Apply LetsEncrypt patch

It's possible to apply a patch to support the [ISRG Root X1 LetsEncrypt certificate](https://letsencrypt.org/certificates/).
Simply include `--patch` with the command.

```bash
docker run --rm extenda/java8-certificate-test:8u102 \
  --patch \
  testrunner.hiiretail.com \
  letsencrypt.org
```

The patch executes the following `keytool` command.

```bash
usr/bin/keytool -importcert -noprompt \
  -storepass changeit \
  -alias isrgrootx1 \
  -keystore /usr/lib/jvm/zulu-8-amd64/jre/lib/security/cacerts \
  -file /letsencrypt/isrgroot1.pem
```

The PEM file can be downloaded with `cURL`.

```bash
curl -sS https://letsencrypt.org/certs/isrgrootx1.pem -o isrgrootx1.pem
```

### Java

First compile the program to obtain an executable jar file.

```bash
mvn clean package
java -jar target/java8-certificate-test.jar letsencrypt.org
```

## How to build

```bash
mvn clean package
docker build -t extenda/java8-certificate-test:8u102 .
```