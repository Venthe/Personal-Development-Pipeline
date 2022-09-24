#!/usr/bin/env bash

rm --recursive \
    --force \
    ./tmp/jenkins_agent*
mkdir ./tmp || true
ssh-keygen \
    -t rsa \
    -b 4096 \
    -N "" \
    -q \
    -f ./tmp/jenkins_agent