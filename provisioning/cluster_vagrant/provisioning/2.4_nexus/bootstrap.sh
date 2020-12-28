# #!/bin/env bash

# ./manager.sh POST_blobstores_file helm-hosted
# ./manager.sh POST_blobstores_file helm-group
# ./manager.sh POST_blobstores_file helm-proxy

# # Docker
# ./manager.sh POST_blobstores_file docker-hosted
# ./manager.sh POST_repositories docker/hosted @repository-docker-hosted.json
# ./manager.sh POST_blobstores_file dockerhub-proxy
# ./manager.sh POST_repositories docker/proxy @repository-dockerhub-proxy.json
# ./manager.sh POST_blobstores_file docker-group
# ./manager.sh POST_repositories docker/group @repository-docker-group.json

# ./manager.sh PUT_security_realms_active '[
#     "NexusAuthenticatingRealm",
#     "NexusAuthorizingRealm",
#     "DockerToken",
#     "LdapRealm"
# ]'
# ./manager.sh PUT_anonymous_access true

kubectl patch --namespace=nexus service/nexus-sonatype-nexus  --patch='{
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

# kubectl patch --namespace=nexus deployment/nexus-sonatype-nexus --patch='{
# "spec": {
#     "template": {
#         "spec":{
#             "containers": [
#                 {
#                     "name": "nexus",
#                     "ports": [
#                         {
#                             "containerPort": 5001,
#                             "name": "nexus-dg",
#                             "protocol": "TCP"
#                         }
#                     ]
#                 }
#             ]
#         }
#     }
# }
# }'
