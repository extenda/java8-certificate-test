# java8-certificate-test

This is the repository for a Docker image that can be used to test LetsEncrypt compatibility on old Java runtimes.

## Usage

```bash
docker run --rm eu.gcr.io/extenda/java8-certificate-test:8u102 \
  testrunner.hiiretail.com \
  letsencrypt.org \
  quotes.retailsvc.com/health
```
The command will fail with exit 1 if it fails to connect to any of the domains.

### Apply LetsEncrypt patch

It's possible to apply a patch to support the ISRG Root X1 LetsEncrypt certificate.
Simply include `--patch` with the command.

```bash
docker run --rm eu.gcr.io/extenda/java8-certificate-test:8u102 \
  --patch \
  testrunner.hiiretail.com \
  letsencrypt.org \
  quotes.retailsvc.com/health
```

## How to build

```bash
mvn clean package
docker build -t eu.gcr.io/extenda/java8-certificate-test:8u102 .
```