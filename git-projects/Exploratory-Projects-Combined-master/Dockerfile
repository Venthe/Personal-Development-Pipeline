ARG GRADLE_VERSION=6.7.0-jdk11
ARG GRADLE_IMAGE=gradle
ARG JRE_VERSION=11.0.4_11-jre-openj9-0.15.1
ARG JRE_IMAGE=adoptopenjdk

FROM ${GRADLE_IMAGE}:${GRADLE_VERSION} as dependencies
ARG build=latest
ARG appName=app
LABEL image=dependencies \
      build=${build} \
      appName=${appName}
RUN mkdir /workdir
WORKDIR /workdir
#COPY build.gradle gradle.properties settings.gradle ./
# Eat the expected build failure since no source code has been copied yet
#RUN gradle clean build --no-daemon --warning-mode=all > /dev/null 2>&1 || true

FROM dependencies as clean-install
ARG build=latest
ARG appName=app
LABEL image=clean-install \
      build=${build} \
      appName=${appName}
COPY ./ ./

#FROM clean-install as test
#ARG build=latest
#ARG appName=app
#LABEL image=test \
#      build=${build} \
#      appName=${appName}
#RUN gradle --stacktrace \
#           --no-daemon \
#           test

FROM clean-install as package
ARG build=latest
ARG appName=app
LABEL image=package \
      build=${build} \
      appName=${appName}
RUN gradle build \
--console=plain \
--stacktrace \
-DappJar=app \
-PskipTests=true \
--warning-mode=all
#--no-daemon \
#           --offline \

FROM ${JRE_IMAGE}:${JRE_VERSION} as final
ARG build=latest
ARG appName=app
LABEL build=${build} \
      appName=${appName} \
      image=final
RUN mkdir /opt/app
COPY --from=package /workdir/build/libs/app.jar /opt/app/app.jar
ENTRYPOINT java -jar /opt/app/app.jar
