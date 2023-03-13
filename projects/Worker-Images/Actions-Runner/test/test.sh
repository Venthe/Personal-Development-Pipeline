#!/usr/bin/env bash

set -o errexit
set -o pipefail
#set -o xtrace

cp /root/.ssh_test /root/.ssh -R && chown -R root /root/.ssh
cp /root/.kube_test /root/.kube -R && chown -R root /root/.kube

echo ""
echo "**************************"
echo "TEST START"
echo "**************************"
echo ""

node --enable-source-maps /runner/index.js || true

cat "/runner/result.json" | grep success

echo ""
echo "**************************"
echo "TEST END"
echo "**************************"
echo ""
