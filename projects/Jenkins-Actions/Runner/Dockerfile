FROM docker.io/library/ubuntu:22.10
WORKDIR /tmp
RUN apt-get update && \
    apt-get install git curl unzip --assume-yes && \
    apt-get clean

WORKDIR /workspace

    # \
    # curl https://nodejs.org/dist/v16.15.1/node-v16.15.1-linux-x64.tar.xz -L --remote-name && \
    # mkdir -p /usr/local/lib/nodejs && \
    # ls && \
    # tar -xJvf ./node-v16.15.1-linux-x64.tar.xz -C /usr/local/lib/nodejs && \
    # echo 'export PATH=/usr/local/lib/nodejs/node-v16.15.1-linux-x64/bin:$PATH' > ~/.profile && \
    # . ~/.profile && \