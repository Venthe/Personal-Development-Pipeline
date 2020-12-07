#!/bin/env bash

. .env

NAMESPACE=${NAMESPACE:=nexus}
WIPE=${WIPE:=false}
ADD_REPO=${ADD_REPO:=false}
UPDATE_REPO=${UPDATE_REPO:=false}
PULL=${PULL:=false}
UPGRADE=${UPGRADE:=true}

if [[ ${WIPE} == true ]]; then
  kubectl delete namespace "${NAMESPACE}"
fi

if [[ ${ADD_REPO} == true ]]; then
  helm repo add oteemocharts https://oteemo.github.io/charts
fi

if [[ ${UPDATE_REPO} == true ]]; then
  helm repo update
fi

if [[ ${PULL} == true ]]; then
  helm pull oteemocharts/sonatype-nexus
fi

if [[ ${UPGRADE} == true ]]; then
  helm upgrade --install \
    --create-namespace --namespace=${NAMESPACE} \
    --values=values.yml \
    nexus \
    oteemocharts/sonatype-nexus
fi
