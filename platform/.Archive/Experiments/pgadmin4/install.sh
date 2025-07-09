#!/usr/bin/env bash

set -x
set -o errexit

docker build . --tag docker.home.arpa/dpage/pgadmin4:6.20
docker login docker.home.arpa
docker push docker.home.arpa/dpage/pgadmin4:6.20
