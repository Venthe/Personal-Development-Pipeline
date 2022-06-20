FROM alpine:3.12.0

RUN apk update
RUN apk add git
RUN apk add openssh
VOLUME ["/workdir"]
RUN mkdir /workcopy
ADD prepare-repository.sh /
RUN ["chmod", "+x", "prepare-repository.sh"]
CMD ["./prepare-repository.sh"]