#!/bin/env bash

PLAYBOOK_PATH=${1:-2_node/control-plane.yml}
MACHINE_NAME=${2:-test-k8s-main-node-1}
PROVIDER=${4:-hyperv}
INVENTORY=${3:-192.168.2.70}

ansible-playbook ./ansible/$PLAYBOOK_PATH --key-file ./.vagrant/machines/$MACHINE_NAME/$PROVIDER/private_key --user vagrant --inventory $INVENTORY,