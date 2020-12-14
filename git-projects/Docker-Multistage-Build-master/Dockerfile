# syntax = docker/dockerfile:1.1.7
ARG VERSION=SNAPSHOT
ARG APP_NAME=mrp

FROM gradle:6.7.1-jdk11 as build
LABEL version="${APP_NAME}:${VERSION}" \
      stage="build"
COPY . ./
RUN gradle build \
      --console=plain \
      --info \
      --stacktrace \
      --no-daemon

FROM build as test
LABEL version="${APP_NAME}:${VERSION}" \
      stage="test"
RUN gradle test \
      --console=plain \
      --info \
      --stacktrace \
      --no-build-cache \
      | tee -a test-result.txt
COPY --from=build \
      /home/gradle/test-results.txt ./tmp/test-results.txt

FROM openjdk:11.0.9.1-jre as ship
LABEL version="${APP_NAME}:${VERSION}" \
      stage="ship"
EXPOSE 8080
COPY --from=build \
      /home/gradle/build/libs/ms-project-0.0.1-SNAPSHOT.jar /opt/app.jar
ENTRYPOINT ["java","-jar","/opt/app.jar"]
