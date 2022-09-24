package eu.venthe.jenkins.actions

sh script: '''\
apt-get update
apt-get install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    --assume-yes
mkdir -p /etc/apt/keyrings

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  focal stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update
apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin \
    --assume-yes
service docker start
''', label: stepName

return [defaultRun: "", status: "ok"]
