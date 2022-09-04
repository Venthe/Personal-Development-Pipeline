#!/usr/bin/env bash

docker run \
    --interactive \
    --env "NAMESPACE=external-dns" \
    --tty \
    --rm \
    "docker.io/venthe/cfssl-k8s:latest"
