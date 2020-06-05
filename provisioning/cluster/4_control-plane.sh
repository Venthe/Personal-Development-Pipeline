#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

kubeadm config images pull

kubeadm init

#kubeadm token create --print-join-command

exit

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

SERVER_ADDRESS=$(cat $HOME/.kube/config | grep server | awk '{print $2}')
TOKEN=$(kubeadm --skip-headers token list | grep authentication | awk '{print $1}')
TOKEN_CA_CERT_HASH=$(openssl x509 -in /etc/kubernetes/pki/ca.crt -noout -pubkey | openssl rsa -pubin -outform DER 2>/dev/null | sha256sum | cut -d' ' -f1)

#kubectl apply -f https://docs.projectcalico.org/v3.14/manifests/calico.yaml
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml