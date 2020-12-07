```bash
#!/usr/bin/env bash

sudo apt install python python3-pip --assume-yes
pip3 install ansible --user
echo 'PATH=$HOME/.local/bin:$PATH' >> ~/.bashrc
ansible-galaxy collection install kubernetes.core
```