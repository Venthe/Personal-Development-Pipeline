#!/usr/bin/env bash

set -e

NAMESPACE=${NAMESPACE:-kube-system}
NAME=${NAME:-etcd-client-tls}
CLUSTER_DOMAIN=${CLUSTER_DOMAIN:-cluster.local}
EXPIRY=${EXPIRY:-87600h} # 87600h -> 10 years

# Using cfssl generate the Configure CA options
cfssl print-defaults config > ca-config.json
cfssl print-defaults csr > ca-csr.json


cat ca-config.json \
    | jq ". \
        | .signing.default.expiry = \"${EXPIRY}\" \
        | del(.signing.profiles.www) \
        | .signing.profiles.server = {\"expiry\":\"${EXPIRY}\", \"usages\": [\"signing\",\"key encipherment\",\"server auth\"]} \
        | .signing.profiles.client = {\"expiry\":\"${EXPIRY}\", \"usages\": [\"signing\",\"key encipherment\",\"client auth\"]} \
        | .signing.profiles.peer = {\"expiry\":\"${EXPIRY}\", \"usages\": [\"signing\",\"key encipherment\",\"server auth\",\"client auth\"]} \
        " \
    > ca-config.modified.json

cat ca-csr.json \
    | jq ". \
        | .CN = \"Local cluster ETCD\" \
        | .key.algo=\"rsa\" | .key.size=2048 \
        | .names[0].\"C\" = \"INT\" \
        | .names[0].\"ST\" = \"Dummy\" \
        | .names[0].\"L\" = \"Dummy\" \
        | del(.hosts) \
        " \
    > ca-csr.modified.json

# Generate CA certificate
cfssl gencert \
        -initca ca-csr.modified.json \
        2>/dev/null \
    | cfssljson -bare ca - \
    > /dev/null

#  Generate Client certificate
echo '{"CN":"root","hosts":[""],"key":{"algo":"rsa","size":2048}}' \
    | cfssl gencert \
        -ca=ca.pem \
        -ca-key=ca-key.pem \
        -config=ca-config.modified.json \
        -profile=peer \
        -hostname="etcd,etcd-headless,localhost,*.etcd-headless.${NAMESPACE}.svc.${CLUSTER_DOMAIN},127.0.0.1" \
        - 2>/dev/null \
    | cfssljson -bare client \
    > /dev/null

echo -e "\
apiVersion: v1
kind: Secret
metadata:
  name: ${NAME}
  namespace: ${NAMESPACE}
data:
  client.pem: $(base64 --wrap 0 ./client.pem)
  ca.pem: $(base64 --wrap 0 ./ca.pem)
  client-key.pem: $(base64 --wrap 0 ./client-key.pem)
"
