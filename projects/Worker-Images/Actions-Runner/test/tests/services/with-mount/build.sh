#!/usr/bin/env bash

docker build . --tag docker.home.arpa/venthe/test:latest
docker push docker.home.arpa/venthe/test:latest
