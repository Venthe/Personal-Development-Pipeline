#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

# Verify CPU number, has to be greater or equal 2
if [[ $(cat /proc/cpuinfo | grep processor | wc --lines) -lt 2 ]]; then echo "There has to be at least two processors"; exit 1; fi

# Verify RAM, has to be greater or equal 2048
if [[ $(free -m | grep "Mem:" | awk '{print $2}') -lt 1922 ]]; then echo "There has to be at least 2048mb of ram"; exit 1; fi

# Verify MAC, Has to be unique
ip addr show | grep link/ether | awk '{print $2}'

# Has to be unique
cat /sys/class/dmi/id/product_uuid

#
ip add | grep inet | grep global | awk '{print $2'}

# Verify ports
function join_by() { local IFS="$1"; shift; echo "$*"; }
function check_port() {
    if [[ $(ss -tulpn | grep -E ":($(join_by "|" ${@}))" | wc --lines) -gt 0 ]]; then echo "At least one port from range is blocked"; exit 1; fi;
}
set +o xtrace
check_port 6443 2379 2380 10250 10251 10252
check_port $(seq 30000 32767)
set -o xtrace
