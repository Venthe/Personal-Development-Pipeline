FROM docker.io/cfssl/cfssl:1.6.1

RUN rm --recursive --force /workdir/ && mkdir /workdir/

RUN curl https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 -o /usr/local/bin/jq --location && \
    chmod u=rx,g=rx,o= /usr/local/bin/*

ADD entrypoint.sh ./
RUN chmod u=rx,g=rx,o= entrypoint.sh

ENTRYPOINT [ "./entrypoint.sh" ]