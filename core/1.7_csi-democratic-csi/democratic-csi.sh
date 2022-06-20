#!/usr/bin/env bash

NAMESPACE=democratic-csi

helm repo add democratic-csi https://democratic-csi.github.io/charts/
helm repo update

kubectl delete namespace $NAMESPACE

helm upgrade \
--install \
--create-namespace \
--values freenas-nfs.yaml \
--namespace $NAMESPACE \
zfs-nfs democratic-csi/democratic-csi

helm upgrade \
--install \
--create-namespace \
--values freenas-iscsi.yaml \
--namespace $NAMESPACE \
zfs-iscsi democratic-csi/democratic-csi