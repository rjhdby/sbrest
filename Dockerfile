FROM openjdk:13-alpine

ARG app_name="sbrest"

RUN apk update && \
    apk add maven && \
    mkdir -p /sbrest

COPY ./src /sbrest/src
COPY ./pom.xml /sbrest

WORKDIR /sbrest

RUN mvn clean package

WORKDIR /

EXPOSE 433

CMD ["java", "-jar", "-XX:+UseContainerSupport", "-Xms1g", "-Xmx1g", "/sbrest/target/sbtest-0.0.1-SNAPSHOT.jar"]