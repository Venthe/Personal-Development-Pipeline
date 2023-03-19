FROM docker.io/library/nginx:1.23.3
RUN apt update \
    && apt --assume-yes install \
      git \
      fcgiwrap \
      apache2-utils \
      rsync \
    && apt clean
RUN chown -R www-data. /usr/share/nginx/html
