#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

# From kubeadm init
kubeadm join 192.168.1.36:6443 \
  --token niwmbf.skcqj73084uoornk \
  --discovery-token-ca-cert-hash sha256:be3e019bf5976ba4187ae2f010ec516edd51509d0873cf46afd0679e4f60f47f