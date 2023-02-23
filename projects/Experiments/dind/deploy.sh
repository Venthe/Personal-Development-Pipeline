#!/usr/bin/env bash

set -o errexit
set -o pipefail

#helm upgrade --install --namespace=infrastructure docker `pwd`/

mkdir -p dist && helm package -d dist ./

helm plugin install https://github.com/chartmuseum/helm-push || true
helm repo add nexus https://nexus.home.arpa/repository/helm-hosted/ || true
helm cm-push \
--username=admin \
--password=secret \
./dist/docker-0.1.0.tgz \
--context-path=/repository/helm-hosted/ \
nexus

helm repo update
helm search repo nexus
