#!/usr/bin/env bash

NAMESPACE=external-dns

kubectl delete namespace $NAMESPACE

helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add coredns https://coredns.github.io/helm

helm repo update

kubectl create namespace $NAMESPACE

bash -c 'cd cfssl && ./cfssl.sh'

helm upgrade \
    --install \
    --version=6.13.3 \
    --create-namespace \
    --values=etcd-values.yml \
    --namespace=${NAMESPACE} \
    etcd bitnami/etcd
helm upgrade \
  --install \
  --create-namespace \
  --values=external-dns-values.yml \
  --namespace=${NAMESPACE} \
  external-dns bitnami/external-dns
helm upgrade \
   --install \
   --create-namespace \
   --values=coredns-values.yml \
   --namespace=${NAMESPACE} \
   coredns coredns/coredns

kubectl apply -f load-balancer.yml