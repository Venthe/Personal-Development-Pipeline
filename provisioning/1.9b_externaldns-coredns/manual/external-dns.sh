#!/usr/bin/env bash
set -x

NAMESPACE=external-dns

helm uninstall --namespace $NAMESPACE external-dns
helm uninstall --namespace $NAMESPACE etcd
helm uninstall --namespace $NAMESPACE coredns
kubectl delete namespace $NAMESPACE

helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add coredns https://coredns.github.io/helm

helm repo update

kubectl create namespace $NAMESPACE

bash -c 'cd cfssl && ./cfssl.sh'
helm upgrade \
    --install \
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