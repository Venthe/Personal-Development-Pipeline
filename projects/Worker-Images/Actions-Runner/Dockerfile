FROM "${RUNNER_BASE_IMAGE:-docker.io/library/ubuntu:22.10}"

RUN apt-get update \
    && apt-get install --assume-yes software-properties-common \
    && add-apt-repository ppa:git-core/ppa && apt-get update \
    && apt-get install --assume-yes curl git nodejs unzip vim iptables \
    && apt-get clean

RUN curl --fail --silent --show-error --location "${NODE_VERSION:-https://deb.nodesource.com/setup_19.x}" | bash -

RUN apt-get install \
           ca-certificates \
           curl \
           gnupg \
           lsb-release \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg \
    && echo \
         "deb [arch=amd64 signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
         jammy stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null \
    && apt-get update \
    && apt-get install --assume-yes docker-ce docker-ce-cli containerd.io docker-compose-plugin \
    && apt-get clean

RUN apt-get update \
    && apt-get install --assume-yes build-essential zlib1g-dev libffi-dev libssl-dev libbz2-dev libreadline-dev libsqlite3-dev liblzma-dev \
    && apt-get clean

ADD "./dist" "/runner"
ADD "./runner.sh" "/runner/runner.sh"

WORKDIR "/workdir"

RUN mkdir -p \
    "/runner" \
    "/runner/actions" \
    "/runner/bin" \
    "/runner/cache" \
    "/runner/metadata" \
    "/runner/metadata/pipeline" \
    "/runner/metadata/env" \
    "/runner/metadata/secrets" \
    "/workdir"

ENTRYPOINT ["/runner/runner.sh"]
CMD ["run", "--enable-source-maps"]
