#!/bin/env bash

function apply() {
kubectl delete pod/dind

# TODO:
#  Add DOCKER_USERNAME? DOCKER_PASSWORD?
#  Add secret /root/.docker/config.json
#  {
#    "auths": {
#      "docker.home.arpa": {
#        "auth": "ZG9ja2VyOnNlY3JldA=="
#      }
#    }
#  }

cat <<EOF | kubectl apply -f -
---
apiVersion: v1
kind: Pod
metadata:
  name: dind
spec:
  volumes:
  - name: certificate-bundle
    emptyDir: {}
  - name: cacrt
    secret:
      items:
        - key: ca.crt
          path: kubernetes-ca.crt
      secretName: jenkins-ingress-cert
      defaultMode: 0644
  initContainers:
    - name: update-ca-certs
      image: kalaksi/ca-certificates:1.2
      volumeMounts:
      - name: certificate-bundle
        mountPath: /etc/ssl/certs
      - name: cacrt
        mountPath: "/additional_certs"
        readOnly: true
  containers:
      - name: dind
        args:
          - --registry-mirror=https://proxy.docker.home.arpa
          - --debug
      image: docker:20.10.12-dind
      securityContext:
        privileged: true
      volumeMounts:
      - name: certificate-bundle
        mountPath: "/etc/ssl/certs"
        readOnly: true
---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: docker-dind-cert
spec:
  secretName: docker-dind-cert
  privateKey:
    rotationPolicy: Always
  dnsNames:
    - 'test.home.arpa'
  issuerRef:
    name: ca-issuer
    kind: ClusterIssuer
...
EOF
}

function delete_pod() {
  kubectl delete pod/dind
}

function get_pod() {
    kubectl get pod/dind
}

function pod_exec() {
  kubectl exec -it pod/dind -- "$@"
}

function shell() {
    pod_exec sh
}

function logs() {
    kubectl logs dind
}

function describe() {
    kubectl describe pod/dind
}

function login() {
    pod_exec docker login \
        docker.home.arpa \
        --username=admin \
        --password=admin123
}

# function test() {
#     pod_exec docker pull alpine:3
#     pod_exec docker tag alpine:3 docker.home.arpa/alpine:3
#     login
#     pod_exec docker push docker.home.arpa/alpine:3
#     cat <<EOF | kubectl apply -f -
# ---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: test-alpine3
spec:
  replicas: 10
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
        - name: test-git
          image: nexus.nexus:5000/git:3
# ...
# EOF
# }

"$@"