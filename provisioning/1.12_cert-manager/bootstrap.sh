#!/bin/bash

set -e

TEMP_PATH="/tmp/certs"
CA_PATH="/etc/kubernetes/pki"
INTERMEDIATE_CA_FILENAME="company-intermediate-ca"

mkdir --parents "${TEMP_PATH}"
chmod 0777 "${TEMP_PATH}"
cd "${TEMP_PATH}"

function read_cert() {
    echo "-----------Reading PEM $1------------"
    local path=$1
    sudo openssl x509 \
        -in "${path}" \
        -text \
        -noout
}
function read_csr() {
    echo "-----------Reading CSR $1------------"
    local path=$1
    sudo openssl req \
        -in "${path}" \
        -text \
        -noout
}

# read_cert "${CA_PATH}/ca.crt"

# Generate key for intermediate CA
#  -aes256 to protect it with passphrase
openssl genrsa -out "${TEMP_PATH}/${INTERMEDIATE_CA_FILENAME}.key" 4096

cat <<EOF > openssl.cnf
[req]
req_extensions = v3_req
# req_extensions = v3_intermediate_ca
# x509_extensions = v3_req
x509_extensions = v3_intermediate_ca
distinguished_name = req_distinguished_name
prompt = no

[ v3_req ]
extendedKeyUsage = serverAuth, clientAuth, codeSigning, emailProtection
basicConstraints = critical,CA:TRUE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment

[req_distinguished_name]
countryName            = PL
stateOrProvinceName    = Mazovia
localityName           = Pruszkow
organizationName       = Home
commonName             = home.arpa intermediate CA
emailAddress           = jacek.lipiec.bc@gmail.com

[ v3_intermediate_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical,CA:true,pathlen:0
keyUsage = critical,digitalSignature,cRLSign, keyCertSign
EOF

# [ v3_ca ]
# subjectKeyIdentifier=hash
# authorityKeyIdentifier=keyid:always,issuer
# basicConstraints = critical,CA:true

# Generate itermediate CSR for home.arpa
sudo openssl req \
    -config openssl.cnf \
    -new \
    -sha256 \
    -newkey rsa:2048 \
    -nodes \
    -key "${INTERMEDIATE_CA_FILENAME}.key" \
    -out "${INTERMEDIATE_CA_FILENAME}.csr"
# read_csr "${INTERMEDIATE_CA_FILENAME}.csr"
    # -subj "/C=PL/ST=Mazovia/L=Pruszkow/O=None/CN=home.arpa" \

# Sign request with kubernetes
sudo openssl x509 \
    -req \
    -in "${INTERMEDIATE_CA_FILENAME}.csr" \
    -CA "${CA_PATH}/ca.crt" \
    -CAkey "${CA_PATH}/ca.key" \
    -extensions v3_intermediate_ca \
    -CAcreateserial \
    -extfile openssl.cnf \
    -out "${INTERMEDIATE_CA_FILENAME}.crt" \
    -days 500 \
    -sha512

# read_cert "${CA_PATH}/ca.crt"
read_cert "${INTERMEDIATE_CA_FILENAME}.crt"
rm ./chain.crt || true
# cat "${INTERMEDIATE_CA_FILENAME}.crt" "${CA_PATH}/ca.crt" > chain.crt
# read_cert chain.crt

sudo cp "${CA_PATH}/ca.crt" ./k8s-ca.crt

sudo chmod 0777 ./k8s-ca.crt

# openssl rsa -text -noout -in k8s-ca.key

cat "${INTERMEDIATE_CA_FILENAME}.crt" "k8s-ca.crt" > chain.crt

kubectl delete secret --namespace cert-manager ca-key-pair || true
kubectl create secret generic \
    --namespace cert-manager \
    ca-key-pair \
    --from-file=tls.crt=./chain.crt \
   --from-file=tls.key=${INTERMEDIATE_CA_FILENAME}.key

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

cat <<EOF > wildcard.conf
[] req ]
default_bits       = 4096
default_md         = sha512
default_keyfile    = home.arpa.key
prompt             = no
encrypt_key        = no
distinguished_name = req_distinguished_name# distinguished_name
[ req_distinguished_name ]
countryName            = PL
stateOrProvinceName    = Mazovia
localityName           = Pruszkow
organizationName       = Home
commonName             = *.home.arpa
emailAddress           = jacek.lipiec.bc@gmail.com
EOF

# Cleanup
# rm --recursive --force "${TEMP_PATH}"

