wget -O - https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update && sudo apt install packer



packer init .
packer build ubuntu-proxmox.pkr.hcl


netsh interface portproxy add v4tov4 listenport=8336 listenaddress=0.0.0.0 connectport=8336 connectaddress=WSL IP172.25.112.115

PS C:\Users\user> New-NetFirewallRule -DisplayName "WSL2 Port Bridge" -Direction Inbound -Action Allow -Protocol TCP -LocalPort 8336