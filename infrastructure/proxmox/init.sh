#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o xtrace

VM_PASSWORD_PLAIN=$(yq eval '.vm_password_plain' .variables.yml)
VM_PASSWORD_HASHED=$(openssl passwd -6 $VM_PASSWORD_PLAIN)
VM_SSH_PUBLIC_KEY=$(cat ~/.ssh/id_rsa.pub)
VM_SSH_PRIVATE_KEY=$(cat ~/.ssh/id_rsa)

yq eval -o=json .variables.yml > .variables.json

# -debug \
# cd packer && PACKER_LOG=1 packer build \
#   -var-file "../.variables.json" \
#   -var 'vm_ssh_pubkeys=["'"$VM_SSH_PUBLIC_KEY"'"]' \
#   -var "vm_password_hashed=$VM_PASSWORD_HASHED" \
#   . && cd -
# netsh interface portproxy add v4tov4 listenport=8336 listenaddress=0.0.0.0 connectport=8336 connectaddress=

terraform -chdir=terraform init -upgrade
# TF_LOG=DEBUG

terraform -chdir=terraform destroy \
  -var-file="../.variables.json" \
  -var 'vm_ssh_pubkeys=["'"$VM_SSH_PUBLIC_KEY"'"]' \
  -var="inventory_path=../inventory.yml" \
  -var "vm_password_hashed=$VM_PASSWORD_HASHED" \
  -auto-approve
terraform -chdir=terraform apply \
  -var-file="../.variables.json" \
  -var 'vm_ssh_pubkeys=["'"$VM_SSH_PUBLIC_KEY"'"]' \
  -var="inventory_path=../inventory.yml" \
  -var "vm_password_hashed=$VM_PASSWORD_HASHED" \
  -auto-approve
