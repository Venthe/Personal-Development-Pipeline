#!/usr/bin/env bash

set -x

NAMESPACE=${NAMESPACE:-external-dns}

function jq() {
    docker run \
        --rm \
        --interactive \
        docker.io/apteno/alpine-jq:2022-02-01 \
        jq "${@}"
}

function yq() {
  docker run \
    --rm \
    --interactive \
    --volume "${PWD}":/workdir \
    mikefarah/yq:4.18.1 \
    "$@"
}

function _cfssl() {
    local cmd=$1
    shift
    docker run \
        --rm \
        --tty=${TTY:-false} \
        --volume "${PWD}:/workdir" \
        --interactive \
        --entrypoint="${cmd}" \
        docker.io/cfssl/cfssl:1.6.1 \
        "${@}"
}

function cfssl() {
    _cfssl cfssl "${@}"
}

function cfssljson() {
    _cfssl cfssljson "${@}"
}

function read_csr() {
    openssl req -text -noout -verify -in "$1.csr"
}

function read_pem() {
    openssl x509 -in $1.pem -noout -text
}

function generate() {
    cfssl gencert \
        -ca=ca.pem \
        -ca-key=ca-key.pem \
        -config=ca-config.modified.json \
        -profile=$1 \
        -hostname=$2 \
        - \
        | cfssljson -bare $3
}

function validate() {
    read_csr $1
    read_pem $1
}

function pem_to_crt() {
    openssl x509 -outform der -in $1.pem -out $2.crt
}

function PKCS1_to_PKCS8() {
    openssl pkcs8 -topk8 -nocrypt -in ./$1.pem -out $2.key
}

if [[ "$#" -ne 0 ]]; then
  TTY=true _cfssl $@
  exit $?
fi

rm *.csr *.pem *.json *.crt

# # Using cfssl generate the Configure CA options

cfssl print-defaults config | tee ca-config.json
cfssl print-defaults csr | tee ca-csr.json

# 87600h -> 10 years
cat ca-config.json | jq ". \
    | .signing.default.expiry = \"87600h\" \
    | del(.signing.profiles.www) \
    | .signing.profiles.server = {\"expiry\":\"87600h\", \"usages\": [\"signing\",\"key encipherment\",\"server auth\"]} \
    | .signing.profiles.client = {\"expiry\":\"87600h\", \"usages\": [\"signing\",\"key encipherment\",\"client auth\"]} \
    | .signing.profiles.peer = {\"expiry\":\"87600h\", \"usages\": [\"signing\",\"key encipherment\",\"server auth\",\"client auth\"]} \
    " \
    | tee ca-config.modified.json

cat ca-csr.json | jq ". \
    | .CN = \"Local cluster ETCD\" \
    | .key.algo=\"rsa\" | .key.size=2048 \
    | .names[0].\"C\" = \"PL\" \
    | .names[0].\"ST\" = \"Masovia\" \
    | .names[0].\"L\" = \"Warsaw\" \
    | del(.hosts) \
    " \
    | tee ca-csr.modified.json

# Generate CA certificate
cfssl gencert -initca ca-csr.modified.json | cfssljson -bare ca -
# validate ca

# Generate Server certificate

#172.16.164.101,e11k8setcd01.mercury.corp,e11k8setcd01.local,e11k8setcd01
# echo '{"CN":"etcd-0","hosts":[""],"key":{"algo":"rsa","size":2048}}' \
    # | generate server "etcd-0" server
# validate server

# Generate Peer certificate

# echo '{"CN":"etcd-0","hosts":[""],"key":{"algo":"rsa","size":2048}}' \
    # | generate peer "etcd-0,etcd,etcd-headless,*.etcd-headless.external-dns.svc.cluster.local" peer
# validate peer

# We will need to run above commands for all the etcd nodes, which is 5 times.

#  Generate Client certificate

# Finally, let's generate the client certs for all the client need to connect to the etcd cluster

echo '{"CN":"root","hosts":[""],"key":{"algo":"rsa","size":2048}}' \
    | generate peer "etcd,etcd-headless,localhost,*.etcd-headless.external-dns.svc.cluster.local" client
# echo '{"CN":"etcd-headless","hosts":[""],"key":{"algo":"rsa","size":2048}}' \
#     | generate peer "etcd,etcd-headless,localhost,*.etcd-headless.external-dns.svc.cluster.local" peer
# validate client

# pem_to_crt client-e11k8setcd01 etcd-client
# pem_to_crt ca etcd-ca
# PKCS1_to_PKCS8 client-e11k8setcd01-key etcd-client
    # --from-file=peer.pem=./peer.pem \
    # --from-file=peer-key.pem=./peer-key.pem \

kubectl delete secret/etcd-client-tls --namespace=$NAMESPACE
kubectl create secret generic \
    --from-file=client.pem=./client.pem \
    --from-file=ca.pem=./ca.pem \
    --from-file=client-key.pem=./client-key.pem \
    --namespace=$NAMESPACE \
    etcd-client-tls

# kubectl delete secret/etcd-peer-tls --namespace=$NAMESPACE
# kubectl create secret generic \
#     --from-file=etcd-peer.pem=./peer.pem \
#     --from-file=etcd-ca.pem=./ca.pem \
#     --from-file=etcd-peer-key.pem=./peer-key.pem \
#     --namespace=$NAMESPACE \
#     etcd-peer-tls

# kubectl get secret/etcd-client-tls --namespace=$NAMESPACE --output=json \
#     | jq "\
#     . \
#     | .data=(\
#         .data \
#         | with_entries(.value|=@base64d) \
#     ) \
#     " \
#     | yq -P '.' -