#!/usr/bin/env bash

DOCKER_BUILDKIT=0 docker build . \
  --tag docker.io/venthe/jenkins-slave:latest \
  --tag venthe/jenkins-slave:latest \
  --tag docker.io/venthe/jenkins-slave:latest-jdk11
