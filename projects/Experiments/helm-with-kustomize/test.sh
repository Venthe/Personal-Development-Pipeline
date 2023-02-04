#!/usr/bin/env bash

set -o pipefail
set -o errexit

helm install \
    --dry-run \
    --namespace "test" \
    --debug \
    --generate-name \
    --post-renderer "./kustomize" \
    --values "./value.yml" \
    hashicorp/vault
