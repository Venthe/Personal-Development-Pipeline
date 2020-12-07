# Prividing with vagrant

## WSL2 or linux

### Vagrant

```bash
curl -O https://releases.hashicorp.com/vagrant/2.2.14/vagrant_2.2.14_x86_64.deb \
    && sudo dpkg -i vagrant_2.2.14_x86_64.deb \
    && rm vagrant_2.2.14_x86_64.deb

# Allow disks to be programatically changed by vagrant
echo 'export VAGRANT_EXPERIMENTAL="disks"' >> ~/.bashrc

# WSL2 Only
echo 'export VAGRANT_WSL_WINDOWS_ACCESS_USER_HOME_PATH="/mnt/f"' >> ~/.bashrc
echo 'export VAGRANT_WSL_ENABLE_WINDOWS_ACCESS="1"' >> ~/.bashrc
# echo 'export VAGRANT_DEFAULT_PROVIDER=hyperv' >> ~/.bashrc

. ~/.bashrc
```

### Ansible

```bash
sudo apt install python python3-pip --assume-yes
pip3 install ansible --user

# Add to bashrc to discover ansible 
echo 'PATH=$HOME/.local/bin:$PATH' >> ~/.bashrc

. ~/.bashrc
```

```bash
# Set in /etc/wsl.conf I have the follow configuration
[automount]
enabled = true
root = /mnt/
options = "metadata,umask=22,fmask=11"
```

```powershell
Restart-Service -Name "LxssManager"
```

then run `./bootstrap.sh`

## Warning! There is a bug in WSL2 and `VAGRANT_EXPERIMENTAL="disks"`

Vagrant up creates additional disks in directory /mnt/DRIVE_LETTER/... but places it in DRIVE_LETTER:\mnt\DRIVE_LETTER\... and because of that it is not cleared with `vagrant destroy`
