#!/usr/bin/bash

set -e

MACHINE_NAME=${MACHINE_NAME:-kubernetes-main-node}
PROVIDER=${PROVIDER:-hyperv}
INVENTORY=$(vagrant ssh-config ${MACHINE_NAME} | grep HostName | awk '{print $2}')

echo "Provider: ${PROVIDER}"
echo "Inventory: ${INVENTORY}"

function provision() {
    PLAYBOOK_PATH=${1}
    shift
    
    ansible-playbook ./$PLAYBOOK_PATH --key-file ./.vagrant/machines/$MACHINE_NAME/$PROVIDER/private_key --user vagrant --inventory $INVENTORY, $@
}

function get_dns() {
    kubectl --namespace=external-dns get service coredns-public --output jsonpath='{.status.loadBalancer.ingress[*].ip}'
}

if [[ "$#" -ne 0 ]]; then
    provision "$@"
else
    provision ./provisioning/1.8_loadbalancer.yml
    provision ./provisioning/1.9b_externaldns-coredns/ansible.yml
    provision ./provisioning/1.10_ingress.yml
    provision ./provisioning/1.11_dashboard.yml
    provision ./provisioning/2.1_ldap.yml --extra-vars '
    {
        "namespace": "ldap",
        "admin_password": "secret",
        "ldap_domain": "home.arpa",
        "ldap_organisation": "My Company",
        "should_clear": false
    }
    '
    IP=$(get_dns)
    MACHINE_NAME=kubernetes-main-node ./provision.sh ./configuration/dns.yml --extra-vars ip="${IP}"
    MACHINE_NAME=kubernetes-worker-node-1 ./provision.sh ./configuration/dns.yml --extra-vars ip="${IP}"
    MACHINE_NAME=kubernetes-worker-node-2 ./provision.sh ./configuration/dns.yml --extra-vars ip="${IP}"
    MACHINE_NAME=kubernetes-worker-node-3 ./provision.sh ./configuration/dns.yml --extra-vars ip="${IP}"
    MACHINE_NAME=kubernetes-main-node ./provision.sh ./configuration/ca-cert.yml --extra-vars cert_path="ca.cert.crt"
    MACHINE_NAME=kubernetes-worker-node-1 ./provision.sh ./configuration/ca-cert.yml --extra-vars cert_path="ca.cert.crt"
    MACHINE_NAME=kubernetes-worker-node-2 ./provision.sh ./configuration/ca-cert.yml --extra-vars cert_path="ca.cert.crt"
    MACHINE_NAME=kubernetes-worker-node-3 ./provision.sh ./configuration/ca-cert.yml --extra-vars cert_path="ca.cert.crt"
    kubectl create configmap ca-certificates.crt --from-file=ca-certificates.crt=./configuration/ca-certificates.crt
    MACHINE_NAME=kubernetes-main-node ./provision.sh ./configuration/add-registry.yml
    MACHINE_NAME=kubernetes-worker-node-1 ./provision.sh ./configuration/add-registry.yml
    MACHINE_NAME=kubernetes-worker-node-2 ./provision.sh ./configuration/add-registry.yml
    MACHINE_NAME=kubernetes-worker-node-3 ./provision.sh ./configuration/add-registry.yml
fi
