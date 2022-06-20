#!/usr/bin/env bash

rm -rf ./democratic-csi

cat <<EOF > .gitignore
democratic-csi/
EOF

git clone https://github.com/democratic-csi/charts.git ./democratic-csi
(cd democratic-csi && git pull --all)

NAMESPACE=democratic-csi

helm uninstall --namespace $NAMESPACE zfs-local-dataset || true

kubectl delete namespace $NAMESPACE

helm upgrade \
--install \
--create-namespace \
--values zfs-local-dataset.values.yaml \
--namespace $NAMESPACE \
zfs-local-dataset ./democratic-csi/stable/democratic-csi/