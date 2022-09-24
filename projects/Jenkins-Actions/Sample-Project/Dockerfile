FROM openjdk:17.0.2-slim-buster

WORKDIR /workdir

ADD build/libs/application.jar application.jar

ENTRYPOINT ["java", "-jar", "/workdir/application.jar"]