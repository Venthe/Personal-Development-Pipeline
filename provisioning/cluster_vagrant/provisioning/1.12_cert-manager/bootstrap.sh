#!/bin/bash

TEMP_PATH="/tmp/certs"
CA_PATH="/etc/kubernetes/pki"
INTERMEDIATE_CA_FILENAME="company-intermediate-ca"

mkdir --parents "${TEMP_PATH}"
chmod 0777 "${TEMP_PATH}"
cd "${TEMP_PATH}"

function read_cert() {
    local path=$1
    sudo openssl x509 \
        -in "${path}"
         \
        -text \
        -noout
}

# read_cert "${CA_PATH}/ca.crt"

# Generate key for intermediate CA
#  -aes256 to protect it with passphrase
openssl genrsa -out "${TEMP_PATH}/${INTERMEDIATE_CA_FILENAME}.key" 8192

cat <<EOF > openssl.cnf
[req]
req_extensions = v3_req
distinguished_name = req_distinguished_name
x509_extensions = v3_intermediate_ca

[req_distinguished_name]

[ v3_req ]
extendedKeyUsage = serverAuth, clientAuth, codeSigning, emailProtection
basicConstraints = CA:TRUE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment

[ v3_ca ]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid:always,issuer
basicConstraints = critical,CA:true

[ v3_intermediate_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true, pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign
EOF

# Generate itermediate CSR
sudo openssl req \
    -config openssl.cnf \
    -new \
    -sha256 \
    -nodes \
    -key "${INTERMEDIATE_CA_FILENAME}.key" \
    -subj "/C=PL/ST=None/L=None/O=None/CN=example.org" \
    -out "${INTERMEDIATE_CA_FILENAME}.csr"
# read_cert "${INTERMEDIATE_CA_FILENAME}.csr"

sudo openssl x509 \
    -req \
    -in "${INTERMEDIATE_CA_FILENAME}.csr" \
    -CA "${CA_PATH}/ca.crt" \
    -CAkey "${CA_PATH}/ca.key" \
    -CAcreateserial \
    -out "${INTERMEDIATE_CA_FILENAME}.crt" \
    -days 500 \
    -sha512

# read_cert "${INTERMEDIATE_CA_FILENAME}.crt"

kubectl delete secret --namespace cert-manager ca-key-pair || true
kubectl create secret tls \
    --namespace cert-manager \
    ca-key-pair \
    --cert "${INTERMEDIATE_CA_FILENAME}.crt" \
    --key "${INTERMEDIATE_CA_FILENAME}.key"

cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: ca-issuer
  namespace: cert-manager
spec:
  ca:
    secretName: ca-key-pair
EOF

cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: selfsigned-issuer
  namespace: cert-manager
spec:
  selfSigned: {}
EOF

# kubectl get ClusterIssuer ca-issuer --namespace cert-manager --output wide

# Cleanup
rm --recursive --force "${TEMP_PATH}"

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: dind
spec:
  containers:
    - name: dind
      args:
        - "--debug"
      image: docker:20.10.0-dind
      securityContext:
        privileged: true
EOF