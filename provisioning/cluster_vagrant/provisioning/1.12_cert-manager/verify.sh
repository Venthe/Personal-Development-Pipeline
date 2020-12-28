#!/bin/bash

kubectl run curl -i --tty --image=curlimages/curl --restart=Never --rm -- https://docker.example.org -vvv
kubectl run openssl -i --tty --image=frapsoft/openssl --restart=Never --rm -- verify -verbose -x509_strict

curl https://docker.example.org -vvv
openssl verify -verbose -x509_strict
openssl s_client -showcerts -servername docker.example.org -connect docker.example.org:443 2>/dev/null