FROM azul/zulu-openjdk:8u102

COPY target/*.jar ./conn.jar
ADD https://letsencrypt.org/certs/isrgrootx1.pem /letsencrypt/isrgroot1.pem

ENTRYPOINT ["/usr/bin/java", "-jar", "conn.jar"]