#!/usr/bin/env bash

helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update
helm upgrade --install --namespace infrastructure reloader stakater/reloader # For helm3 add --generate-name flag or set the release name
