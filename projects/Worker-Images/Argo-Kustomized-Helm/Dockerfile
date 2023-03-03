FROM docker.io/library/alpine:3.17.2

USER 0

WORKDIR /usr/local/bin

RUN apk update && apk upgrade && apk add bash

RUN wget --output-document /usr/local/bin/yq https://github.com/mikefarah/yq/releases/download/v4.31.1/yq_linux_amd64 && \
    chmod +x /usr/local/bin/yq && \
    wget --output-document /usr/local/bin/helm.tgz https://get.helm.sh/helm-v3.11.1-linux-amd64.tar.gz && \
    tar -xf /usr/local/bin/helm.tgz -C /usr/local/bin linux-amd64/helm && \
    mv /usr/local/bin/linux-amd64/helm /usr/local/bin/helm && \
    rm -r /usr/local/bin/linux-amd64 && \
    chmod +x /usr/local/bin/helm && \
    wget --output-document /usr/local/bin/kustomize.tgz https://github.com/kubernetes-sigs/kustomize/releases/download/kustomize%2Fv5.0.0/kustomize_v5.0.0_linux_amd64.tar.gz && \
    tar -xf /usr/local/bin/kustomize.tgz && \
    rm /usr/local/bin/kustomize.tgz && \
    chmod +x /usr/local/bin/kustomize

COPY ./manager.sh /usr/local/bin/manager

RUN adduser -u 1500 -h /home/runner -s /usr/bin/bash -D runner && \
    sed -i 's/1500/999/g' /etc/passwd && \
    chown -R runner /home/runner && \
    chmod u+x,g+x,o+x /usr/local/bin/manager

COPY plugin.yml /home/argocd/cmp-server/config/plugin.yaml

USER 999

WORKDIR /workdir

ARG image_version
ENV IMAGE_VERSION=$image_version

ENTRYPOINT ["manager"]
