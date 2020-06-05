#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

apt-get update \
  && apt-get install --assume-yes \
    apt-transport-https \
    curl

curl --silent https://packages.cloud.google.com/apt/doc/apt-key.gpg \
  | apt-key add -

cat <<EOF | sudo tee /etc/apt/sources.list.d/kubernetes.list
deb https://apt.kubernetes.io/ kubernetes-xenial main
EOF

apt-get update \
  && apt-get install --assume-yes \
    kubelet \
    kubeadm \
    kubectl
apt-mark hold \
  kubelet \
  kubeadm \
  kubectl