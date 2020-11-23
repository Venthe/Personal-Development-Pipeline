#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

RELEASE_NAME="$(lsb_release -cs)"
CONTAINERD_VERSION=1.2.13-2
DOCKER_CE_VERSION=5:19.03.9~3-0~ubuntu-"${RELEASE_NAME}"
DOCKER_CE_CLI_VERSION=5:19.03.9~3-0~ubuntu-"${RELEASE_NAME}"

# Root check
if [[ $EUID -ne 0 ]]; then echo "Run this as the root user"; exit 1; fi

## Set up the repository:
### Install packages to allow apt to use a repository over HTTPS
apt-get update \
  && apt-get install --assume-yes \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common \
    gnupg2

# Add Docker’s official GPG key:
curl --fail \
  --silent \
  --show-error \
  --location https://download.docker.com/linux/ubuntu/gpg \
  | apt-key add -

# Add the Docker apt repository:
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu ${RELEASE_NAME} stable"

# Install Docker CE
apt-get update \
  && apt-get install --assume-yes \
    containerd.io="${CONTAINERD_VERSION}" \
    docker-ce="${DOCKER_CE_VERSION}" \
    docker-ce-cli="${DOCKER_CE_CLI_VERSION}"

# Set up the Docker daemon
cat > /etc/docker/daemon.json <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2"
}
EOF

mkdir -p /etc/systemd/system/docker.service.d

# Restart Docker
systemctl daemon-reload
systemctl restart docker
