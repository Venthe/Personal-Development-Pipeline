#!/usr/bin/env bash

get_statefulsets() {
    kubectl get statefulsets --output=jsonpath="{range .items[?(@.metadata.labels.app=='$1')]} {.metadata.name}{end}"
}

wipe_app() {
    print_header "Wiping $1."
    kubectl delete Service,StatefulSet,ConfigMap,PersistentVolumeClaim,Deployment,Secret -l app="$1"
}

deploy_app() {
    local path="$1"
    local appName="$2"
    print_header "Creating app. appName=$appName, path=$path"
    kubectl apply --filename "./$path/$appName"
}
