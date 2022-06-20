#!/bin/bash

kubectl run curl -i --tty --image=curlimages/curl --restart=Never --rm -- https://docker.home.arpa -vvv
kubectl run openssl -i --tty --image=frapsoft/openssl --restart=Never --rm -- verify -verbose -x509_strict

curl https://docker.home.arpa -vvv
openssl verify -verbose -x509_strict
openssl s_client -showcerts -servername docker.home.arpa -connect docker.home.arpa:443 2>/dev/null