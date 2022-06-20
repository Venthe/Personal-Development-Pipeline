#!/usr/bin/env bash

helm repo add bitnami https://charts.bitnami.com/bitnami

    # --set auth.client.useAutoTLS=true \
    # --set auth.client.secureTransport=true \
    # --set auth.rbac.rootPassword=$(kubectl get secret --namespace "external-dns" etcd -o jsonpath="{.data.etcd-root-password}" | base64 --decode) \
helm upgrade \
    --install \
    --create-namespace \
    --namespace=external-dns \
    etcd bitnami/etcd

helm upgrade \
    --install \
    --create-namespace \
    --namespace=external-dns \
    --values=coredns-values.yml \
    coredns \
    coredns/coredns

 helm upgrade \
    --install \
    --create-namespace \
    --values=external-dns-values.yaml \
    --namespace=external-dns \
    external-dns \
    bitnami/external-dns

kubectl apply -f load-balancer.yml