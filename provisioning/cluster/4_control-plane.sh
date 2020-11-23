#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
# if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

# sudo kubeadm config images pull

sudo kubeadm init

#kubeadm token create --print-join-command

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# For grafana
# _SERVER_ADDRESS=$(cat $HOME/.kube/config | grep server | awk '{print $2}')
# _TOKEN=$(kubeadm --skip-headers token list | grep authentication | awk '{print $1}')
# _TOKEN_CA_CERT_HASH=$(openssl x509 -in /etc/kubernetes/pki/ca.crt -noout -pubkey | openssl rsa -pubin -outform DER 2>/dev/null | sha256sum | cut -d' ' -f1)

# cat ~/.kube/config | grep certificate-authority-data | awk '{print $2}' | base64 --decode
# cat ~/.kube/config | grep client-certificate-data | awk '{print $2}' | base64 --decode
# cat ~/.kube/config | grep client-key-data | awk '{print $2}' | base64 --decode

kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
#kubectl apply -f ./calico.yaml

systemctl status kubelet
#journalctl -xeu kubelet