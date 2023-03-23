FROM docker.io/library/nginx:1.23.3
RUN apt update \
    && apt --assume-yes install \
      git \
      fcgiwrap \
      apache2-utils \
      rsync \
    && apt clean
RUN chown -R www-data. /usr/share/nginx/html
COPY loader.sh /loader.sh
COPY git.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["/loader.sh", "server"]