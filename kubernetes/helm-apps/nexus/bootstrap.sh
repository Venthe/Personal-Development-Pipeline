#!/bin/env bash
set -x

. .env

NAMESPACE=${DEPLOYMENT_NAME} \
    ADD_REPO=true \
    UPDATE_REPO=true \
    ./helm.sh

./manager.sh wait_for_nexus
./manager.sh POST_blobstores_file helm-hosted
./manager.sh POST_blobstores_file helm-group
./manager.sh POST_blobstores_file helm-proxy

# Docker
./manager.sh POST_blobstores_file docker-hosted
./manager.sh POST_repositories docker/hosted @repository-docker-hosted.json
./manager.sh POST_blobstores_file dockerhub-proxy
./manager.sh POST_repositories docker/proxy @repository-dockerhub-proxy.json
./manager.sh POST_blobstores_file docker-group
./manager.sh POST_repositories docker/group @repository-docker-group.json

./manager.sh PUT_security_realms_active '[
    "NexusAuthenticatingRealm",
    "NexusAuthorizingRealm",
    "DockerToken",
    "LdapRealm"
]'
./manager.sh PUT_anonymous_access true

kubectl patch --namespace=nexus service/nexus --patch='{
"spec": {
    "ports": [
        {
            "name": "docker-group",
            "port": 5001,
            "protocol": "TCP"
        }
    ]
}
}'

kubectl patch --namespace=nexus deployment/nexus-sonatype-nexus --patch='{
"spec": {
    "template": {
        "spec":{
            "containers": [
                {
                    "image": "quay.io/travelaudience/docker-nexus:3.27.0",
                    "name": "nexus",
                    "ports": [
                        {
                            "containerPort": 5001,
                            "name": "nexus-dg",
                            "protocol": "TCP"
                        }
                    ]
                }
            ]
        }
    }
}
}'
