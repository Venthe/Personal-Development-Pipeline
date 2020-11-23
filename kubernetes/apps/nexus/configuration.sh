#!/usr/bin/env bash

set -x
set -e

PASSWORD=$(kubectl exec --namespace=nexus-namespace svc/nexus-service -- cat //nexus-data/admin.password)
URL="nexus.local:80/service/rest"

curl -X get -u admin:${PASSWORD} "${URL}/v1/security/user-sources"