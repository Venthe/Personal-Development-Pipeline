#!/usr/bin/env bash

set -o pipefail
set -o errexit

TARGET=`date +"%Y-%m-%d_%H-%M-%S"`
_PWD=`pwd`

mkdir "$TARGET"

cd "$TARGET"

mkdir -p etc/kubernetes/pki
sudo cp -r /etc/kubernetes/pki/* etc/kubernetes/pki/

mkdir -p etcd
sudo ~/nerdctl run --rm -v $(pwd)/etcd:/backup \
    --network host \
    -v etc/kubernetes/pki/etcd:/etc/kubernetes/pki/etcd \
    --env ETCDCTL_API=3 \
    k8s.gcr.io/etcd:3.4.3-0 \
    etcdctl --endpoints=https://127.0.0.1:2379 \
    --cacert=etc/kubernetes/pki/etcd/ca.crt \
    --cert=etc/kubernetes/pki/etcd/healthcheck-client.crt \
    --key=etc/kubernetes/pki/etcd/healthcheck-client.key \
    snapshot save /backup/etcd-snapshot-latest.db

mkdir -p etc/kubernetes
sudo cp /etc/kubernetes/admin.conf etc/kubernetes/

# bootstrap-kubeconfig
mkdir -p etc/kubernetes
sudo cp -R /etc/kubernetes/bootstrap-kubelet.conf etc/kubernetes/ || true

# kubeconfig
mkdir -p etc/kubernetes
sudo cp -R /etc/kubernetes/kubelet.conf etc/kubernetes/ || true

# config
mkdir -p var/lib/kubelet
sudo cp -R /var/lib/kubelet/config.yaml var/lib/kubelet/ || true

# EnvironmentFile
mkdir -p var/lib/kubelet
sudo cp -R /var/lib/kubelet/kubeadm-flags.env var/lib/kubelet/ || true

# EnvironmentFile
mkdir -p etc/default
sudo cp -R /etc/default/kubelet etc/default/ || true

cd $_PWD

ls

sudo tar -zcvf kubernetes-backup-${TARGET}.tar.gz "${TARGET}"

sudo rm -R $TARGET