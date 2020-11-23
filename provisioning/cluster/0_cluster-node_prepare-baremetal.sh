#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

# Configure IP Tables
# traffic being rerouted incorrectly due to bypassing of iptables otherwise
sysctl -w net.ipv4.ip_forward=1
sed -i 's/#net.ipv4.ip_forward=1/net.ipv4.ip_forward=1/g' /etc/sysctl.conf
sysctl -p /etc/sysctl.conf

# Disable swap
swapoff --all
cat <<EOF >> /lib/systemd/system/turnswapoff.service
[Unit]
Description=Turn swap off 

[Service]
ExecStart=/sbin/swapoff -a

[Install]
WantedBy=multi-user.target
EOF
systemctl enable turnswapoff

# Load br_netfilter module
modprobe br_netfilter

# As a requirement for your Linux Node’s iptables to correctly see bridged traffic, you should ensure net.bridge.bridge-nf-call-iptables is set to 1 in your sysctl config, e.g.
cat <<EOF | tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system