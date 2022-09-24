#!/usr/bin/env bash

docker build . \
    --tag=docker.io/venthe/cfssl-k8s:latest \
    --tag=docker.io/venthe/cfssl-k8s:1.6.1
docker login
docker push docker.io/venthe/cfssl-k8s:latest
docker push docker.io/venthe/cfssl-k8s:1.6.1