FROM docker.io/library/eclipse-temurin:17.0.6_10-jre as builder
WORKDIR /application
COPY gerrit_mediator-0.0.1-SNAPSHOT.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM docker.io/library/eclipse-temurin:17.0.6_10-jre

WORKDIR /application

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

RUN mkdir /application/configuration

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
CMD ["--spring.config.additional-location=file:./configuration/"]
