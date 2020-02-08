#!/usr/bin/env bash

delete_all_pods() {
  local namespace="$1"
  print_header "Deleting all pods. namespace=$namespace"
  kubectl delete pod \
    --namespace "$namespace" \
    --all
}

get_all_pods() {
  print_header "Listing all pods."
  kubectl get pod --all-namespaces
}