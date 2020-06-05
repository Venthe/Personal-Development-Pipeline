FROM gradle:6.4.0-jdk11 AS build
WORKDIR /home/gradle/project
COPY . /home/gradle/project

ARG REVISION
ARG APPNAME
FROM openjdk:11.0.7-jre-slim-buster as ship
COPY --from=build /home/gradle/project/app*.jar /app.jar
WORKDIR /config
VOLUME /config
CMD ["java", "-jar", "/app.jar"]