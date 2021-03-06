#!/bin/env bash

function upgrade() {
cat <<EOF | kubectl apply -f -
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dind
spec:
  selector:
    matchLabels:
        container: dind
  template:
    metadata:
      labels:
        container: dind
    spec:
      containers:
        - name: dind
          args:
            # - "--insecure-registry=http://docker.example.org"
            # - "--insecure-registry=http://dockerhub-mirror.example.org"
            # - "--registry-mirror=http://dockerhub-mirror.example.org"
            - "--insecure-registry=http://nexus-sonatype-nexus.nexus:5000"
            - "--insecure-registry=http://nexus-sonatype-nexus.nexus:5001"
            - "--registry-mirror=http://nexus-sonatype-nexus.nexus:5001"
            - "--debug"
          image: docker:20.10.0-dind
          securityContext:
            privileged: true
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: test-dind
spec:
  selector:
    matchLabels:
        container: test-dind
  template:
    metadata:
      labels:
        container: test-dind
    spec:
      containers:
        - name: test-dind
          image: localhost:30516/alpine
...
EOF
}

function get_pod() {
    kubectl get pods -oname --selector container=dind | head
}

function pod_exec() {
  kubectl exec -it $(get_pod) -- "$@"
}

function shell() {
    pod_exec sh
}

function log() {
    kubectl logs $(get_pod)
}

function login() {
    pod_exec docker login \
        nexus.nexus:5000 \
        --username=admin \
        --password=admin123
}

function test() {
    pod_exec docker pull alpine:3
    pod_exec docker tag alpine:3 nexus.nexus:5000/alpine:3
    login
    pod_exec docker push nexus.nexus:5000/alpine:3
    cat <<EOF | kubectl apply -f -
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: test-alpine3
spec:
  selector:
    matchLabels:
      container: test-alpine3
  template:
    metadata:
      labels:
        container: test-alpine3
    spec:
      containers:
        - name: test-alpine3
          image: nexus.nexus:5000/alpine:3
...
EOF
}

"$@"