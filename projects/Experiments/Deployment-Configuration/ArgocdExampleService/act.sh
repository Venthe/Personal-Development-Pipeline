#!/usr/bin/env bash

set -o errexit
set -o pipefail

#mvn package

version="$(date -d @$(git show -s --format=%ct HEAD) +%Y%m%d-%H%M%S)-$(git show -s --format=%h HEAD)"
tag="docker.home.arpa/venthe/argocd-example:${version}"

docker build \
  --file=Dockerfile target/ \
  --tag "${tag}"
docker login docker.home.arpa --username=admin
docker push ${tag}

helm upgrade \
--install \
--namespace=integration \
--create-namespace \
--set "image.tag=${version}" \
argocd ./charts

mkdir -p dist && helm package -d dist ./charts

helm plugin install https://github.com/chartmuseum/helm-push
helm repo add nexus https://nexus.home.arpa/repository/helm-hosted/
helm cm-push \
--username=admin \
--password=secret \
./dist/argocd-example-service-0.1.0.tgz \
--context-path=/repository/helm-hosted/ \
nexus

helm repo update
helm search repo nexus