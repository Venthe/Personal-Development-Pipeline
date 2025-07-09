#cloud-config
autoinstall:
  version: 1
  updates: security
  storage:
    layout:
      name: direct
  apt:
    disable_suites: [security]
    preserve_sources_list: false
    primary:
      - arches: [amd64]
        uri: "http://archive.ubuntu.com/ubuntu"
  identity:
    hostname: "${vm_hostname}"
    username: "${vm_username}"
    password: "${vm_password_hashed}"
  locale: "${vm_locale}"
  packages:
    - qemu-guest-agent
    - sudo
  network:
    version: 2
    ethernets:
      ens18:
        dhcp4: true
        nameservers:
          addresses: [${join(", ", vm_nameservers)}]
  ssh:
    install-server: true
    disable_root: true
    ssh_quiet_keygen: true
    allow-pw: true
    %{ if length(vm_ssh_pubkeys) > 0 }
    authorized-keys:
      %{ for key in vm_ssh_pubkeys ~}
      - ${key}
      %{ endfor }
    %{ else }
    authorized-keys: []
    %{ endif }
  late-commands:
    - curtin in-target -- bash -c 'apt-get update'
    - curtin in-target -- bash -c 'apt-get install --assume-yes qemu-guest-agent ubuntu-standard net-tools'
    - curtin in-target -- bash -c 'systemctl enable ssh'
    - curtin in-target -- bash -c 'systemctl start ssh'
