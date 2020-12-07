#!/bin/env sh

set -x

VERSION=latest
APP_NAME=mrp

BUILDKIT_PROGRESS=plain \
DOCKER_BUILDKIT=1 \
    docker build \
    --progress=plain \
    --file Dockerfile \
    --build-arg VERSION="${VERSION}" \
    --build-arg APP_NAME="${APP_NAME}" \
    --tag venthe/"${APP_NAME}:${VERSION}" \
    .
