#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

_HOST="nfs.local"
_PATH="/srv/nfs"

sudo apt install nfs-common --assume-yes
mkdir /mnt || true
showmount "${_HOST}"
sudo sh -c 'echo "'${_HOST}':'${_PATH}' /mnt nfs4,rw,soft,intr" >> /etc/fstab'
sudo mount -a
sudo mount | grep "${_PATH}"

sudo apt-get install autofs --assume-yes
sudo sh -c 'echo "/mnt -fstype=nfs4,rw,soft,intr" nfs.local:/srv/nfs > /etc/auto.nfsdb'
sudo systemctl restart autofs


# cat <<EOF | kubectl apply -f -
# apiVersion: v1
# kind: PersistentVolume
# metadata:
#   name: nfs-pv
#   labels:
#     name: mynfs # name can be anything
# spec:
#   storageClassName: manual # same storage class as pvc
#   capacity:
#     storage: 200Mi
#   accessModes:
#     - ReadWriteMany
#   nfs:
#     server: ${HOST} # ip addres of nfs server
#     path: ${PATH} # path to directory
# EOF