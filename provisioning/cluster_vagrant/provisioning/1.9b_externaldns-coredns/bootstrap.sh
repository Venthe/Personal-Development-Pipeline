#!/bin/bash

mkdir /tmp/coredns
kubectl get \
    --namespace=kube-system \
    --output=jsonpath='{.data.Corefile}' \
     configmap/coredns  \
    > /tmp/coredns/Corefile
echo "example.org:53 {
    errors
    cache 30
    forward . $(kubectl get service \
    --namespace=external-dns \
    --output=jsonpath='{.status.loadBalancer.ingress[0].ip}' \
    coredns-public)
}" >> /tmp/coredns/Corefile
kubectl patch \
    --namespace=kube-system \
    configmap/coredns \
    --patch="{\"data\":{\"Corefile\":\"$(cat /tmp/coredns/Corefile | awk -v ORS='\\n' '1')\"}}"
rm -rf /tmp/coredns