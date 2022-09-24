FROM jenkins/jenkins:lts-jdk11
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt
USER root
RUN curl -fsSLO https://download.docker.com/$(uname -s | awk '{print tolower($0)}')/static/stable/$(uname -m)/docker-20.10.17.tgz \
  && tar xzvf docker-20.10.17.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-20.10.17.tgz
# RUN apt-get -y update && \
#  apt-get -y install apt-transport-https ca-certificates curl gnupg-agent software-properties-common && \
#  mkdir -p /etc/apt/keyrings && \
#  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
#  add-apt-repository \
#  “deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo “$ID”) \
#  $(lsb_release -cs) \
#  stable” && \
#  apt-get update && \
#  apt-get -y install docker-ce-cli 
# #containerd.io docker-ce 
# RUN curl -L “https://github.com/docker/compose/releases/download/v2.6.0/docker-compose-$(uname -s)-$(uname -m)” -o /usr/local/bin/docker-compose && \
#  chmod +x /usr/local/bin/docker-compose && \
#  ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
USER jenkins