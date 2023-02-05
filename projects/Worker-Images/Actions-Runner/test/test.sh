#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o xtrace

echo "Testing..."

cp /root/.ssh_test /root/.ssh -R && chown -R root /root/.ssh

node --enable-source-maps /runner/index.js
