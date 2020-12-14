#!/bin/bash

export OUTPUT_DIR=output

mkdir -p output
./manager.sh clean
./manager.sh generate_key root.key
./manager.sh generate_pem root.key root.pem
./manager.sh generate_key www.my-domain.com.key
./manager.sh generate_csr www.my-domain.com.key www.my-domain.com.csr
./manager.sh generate_certificate www.my-domain.com.csr root.pem root.key www.my-domain.com.crt www.my-domain.com.ext