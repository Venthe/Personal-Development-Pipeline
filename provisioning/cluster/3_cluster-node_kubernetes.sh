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

# Overlay FS
cat > /etc/docker/daemon.json <<EOF
{
   "exec-opts": ["native.cgroupdriver=systemd"],
   "log-driver": "json-file",
   "log-opts": {
        "max-size": "100m"
   },
   "storage-driver": "overlay2"
}
EOF

# Audit logs
mkdir -p /etc/kubernetes

cat > /etc/kubernetes/audit-policy.yaml <<EOF
apiVersion: audit.k8s.io/v1beta1
kind: Policy
rules:
- level: Metadata
EOF

mkdir -p /var/log/kubernetes/audit

# Install

apt-get update \
  && apt-get install --assume-yes \
    kubelet \
    kubeadm \
    kubectl
apt-mark hold \
  kubelet \
  kubeadm \
  kubectl

  # Configure
sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" >> /etc/sysctl.conf