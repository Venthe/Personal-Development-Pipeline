# #!/bin/env bash

set -e

. .env
. ./manager.sh

POST_blobstores_file helm-hosted
POST_blobstores_file helm-group
POST_blobstores_file helm-proxy

# Docker
POST_blobstores_file docker-hosted
POST_repositories docker/hosted @repository-docker-hosted.json
POST_blobstores_file dockerhub-proxy
POST_repositories docker/proxy @repository-dockerhub-proxy.json
POST_blobstores_file docker-group
POST_repositories docker/group @repository-docker-group.json

PUT_security_realms_active '[
    "NexusAuthenticatingRealm",
    "NexusAuthorizingRealm",
    "DockerToken",
    "LdapRealm"
]'
PUT_anonymous_access true

kubectl patch --namespace=nexus service/nexus-sonatype-nexus  --patch='{
"spec": {
    "ports": [
        {
            "name": "docker-group",
            "port": 5001,
            "protocol": "TCP"
        },
        {
            "name": "docker-hosted",
            "port": 5000,
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
                    "name": "nexus",
                    "ports": [
                        {
                            "containerPort": 5001,
                            "name": "nexus-dg",
                            "protocol": "TCP"
                        },
                        {
                            "containerPort": 5000,
                            "name": "nexus-hosted",
                            "protocol": "TCP"
                        }
                    ]
                }
            ]
        }
    }
}
}'

kubectl patch --namespace=nexus ingress/nexus-sonatype-nexus-docker --patch='{
  "spec": {
    "rules": [
      {
        "host": "docker.example.org",
        "http": {
          "paths": [
            {
              "path": "/",
              "pathType": "ImplementationSpecific",
              "backend": {
                "serviceName": "nexus-sonatype-nexus",
                "servicePort": 5001
              }
            }
          ]
        }
      }
    ]
  }
}'
