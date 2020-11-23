#!/usr/bin/env bash

sudo apt install --assume-yes avahi-daemon

# mDNS
kubectl apply -f https://raw.githubusercontent.com/tsaarni/k8s-external-mdns/master/external-dns-with-avahi-mdns.yaml

# watch --interval 0.5 cat /etc/avahi/hosts