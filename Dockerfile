FROM openjdk:8-jre-alpine

COPY target/webauthenticator-1.0.0-SNAPSHOT.jar webauthenticator.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","webauthenticator.jar"]