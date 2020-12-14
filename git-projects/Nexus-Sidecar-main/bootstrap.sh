#!/bin/bash
set -x

./manager.sh run_in_docker
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

./manager.sh PUT_security_realms_active "[ \"NexusAuthenticatingRealm\", \"NexusAuthorizingRealm\", \"DockerToken\", \"LdapRealm\" ]"
./manager.sh PUT_anonymous_access true
./manager.sh PUT_user_password admin admin