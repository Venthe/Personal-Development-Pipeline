#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

systemctl status nfs-server || true
apt install \
 nfs-kernel-server \
 nfs-common portmap \
 --assume-yes
systemctl start nfs-server
mkdir /srv/nfs/kubedata -p
chmod -R 777 /srv/nfs/kubedata # for simple use but not advised

bash -c 'echo "/srv/nfs/kubedata *(rw,sync,no_subtree_check,no_root_squash,insecure)" >> /etc/exports'
exportfs -rv
showmount --exports