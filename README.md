# java8-certificate-test

This is the repository for a Docker image that can be used to test LetsEncrypt compatibility on Java 8.

## Usage

```bash
docker run --rm eu.gcr.io/extenda/java8-certificate-test:8u102 \
  testrunner.hiiretail.com \
  letsencrypt.org
```
The command will exit with status 1 if it fails to connect to any of the domains.

### Apply LetsEncrypt patch

It's possible to apply a patch to support the [ISRG Root X1 LetsEncrypt certificate](https://letsencrypt.org/certificates/).
Simply include `--patch` with the command.

```bash
docker run --rm eu.gcr.io/extenda/java8-certificate-test:8u102 \
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

## How to build

```bash
mvn clean package
docker build -t eu.gcr.io/extenda/java8-certificate-test:8u102 .
```