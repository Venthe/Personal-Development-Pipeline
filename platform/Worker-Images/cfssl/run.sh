#!/usr/bin/env bash

docker run \
    --interactive \
    --env "NAMESPACE=infrastructure" \
    --tty \
    --rm \
    "docker.io/venthe/cfssl-k8s:latest"
