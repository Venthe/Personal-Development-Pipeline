FROM docker.io/library/eclipse-temurin:17.0.6_10-jre

WORKDIR /application

ADD gerrit_mediator-0.0.1-SNAPSHOT.jar application.jar

RUN mkdir /application/configuration

ENTRYPOINT ["java", "-jar", "application.jar"]
CMD ["--spring.config.additional-location=file:./configuration/"]
