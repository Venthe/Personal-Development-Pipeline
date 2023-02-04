FROM bibinwilson/jenkins-slave:latest

RUN apt-get remove --assume-yes openjdk-8-jdk && \
    apt-get update && \
    apt-get install --assume-yes openjdk-11-jdk && \
    apt-get clean
