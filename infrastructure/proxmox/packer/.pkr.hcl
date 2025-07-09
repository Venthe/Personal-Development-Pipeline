packer {
  required_plugins {
    proxmox = {
      version = ">= 1.2.2"
      source  = "github.com/hashicorp/proxmox"
    }
  }
}

source "proxmox-iso" "ubuntu" {
  # Boot
  boot_command = [
    "<esc><wait>c<wait>",
    "linux /casper/vmlinuz autoinstall debug=true ds='nocloud-net;s=http://{{ .HTTPIP }}:{{ .HTTPPort }}/' ---<enter><wait>",
    "initrd /casper/initrd<enter><wait>",
    "boot<enter><wait>"
  ]

  boot_wait = "5s"
  http_content = {
    "/meta-data" = templatefile("config/meta-data.pkrtpl.hcl", {
      vm_hostname = var.vm_hostname
    })
    "/user-data" = templatefile("config/user-data.pkrtpl.hcl", {
      vm_hostname        = var.vm_hostname
      vm_username        = var.vm_username
      vm_password_hashed = var.vm_password_hashed
      vm_locale          = var.vm_locale
      vm_nameservers     = var.vm_nameservers
      vm_ssh_pubkeys     = var.vm_ssh_pubkeys
    })
  }
  # http_port_max = 8336
  # http_port_min = 8336
  # http_interface = "eth0"
  # http_directory = "config"

  scsi_controller = "virtio-scsi-single"

  # SSH
  #ssh_username            = "autoinstall"
  ssh_username = var.vm_username
  # ssh_username = "autoinstall"
  # ssh_private_key_file    = "~/.ssh/id_rsa"
  ssh_timeout             = "30m"
  cloud_init              = true
  cloud_init_storage_pool = var.proxmox_storage_pool
  ssh_password            = var.vm_password_plain

  serials    = ["socket"]
  qemu_agent = true

  # VM
  vm_name     = var.proxmox_vm_name
  node        = var.proxmox_node
  cores       = 2
  sockets     = 2
  cpu_type    = "host"
  memory      = 2048
  os          = "l26"
  unmount_iso = true

  boot_iso {
    type = "ide"
    # iso_url          = "https://releases.ubuntu.com/25.04/ubuntu-25.04-live-server-amd64.iso"
    # iso_file = "NFS-SERVER:iso/ubuntu-20.04.3-live-server-amd64.iso"
    iso_file     = "local:iso/ubuntu-25.04-live-server-amd64.iso"
    iso_checksum = "sha256:8b44046211118639c673335a80359f4b3f0d9e52c33fe61c59072b1b61bdecc5"
    unmount      = true
    # iso_storage_pool = "local"
  }

  network_adapters {
    model    = "virtio"
    bridge   = "vmbr0"
    firewall = true
  }

  disks {
    type         = "scsi"
    disk_size    = "20G"
    storage_pool = var.proxmox_storage_pool
  }

  # proxmox
  proxmox_url = var.proxmox_url
  username    = var.proxmox_user
  # TOKEN
  password                 = var.proxmox_password
  insecure_skip_tls_verify = true

  template_description = "Ubuntu 25.04 server, generated on ${timestamp()}"
  template_name        = "Ubuntu-25.04-server"
}

build {
  name    = "ubuntu-server-plucky"
  sources = ["source.proxmox-iso.ubuntu"]

  provisioner "file" {
    source      = "files/99-pve.cfg"
    destination = "/tmp/99-pve.cfg"
  }

  provisioner "file" {
    source      = "files/40_setup-serial-access.cfg"
    destination = "/tmp/40_setup-serial-access.cfg"
  }

  provisioner "file" {
    source      = "files/failover-netplan.yaml"
    destination = "/tmp/failover-netplan.yaml"
  }

  provisioner "shell" {
    environment_vars = [
      "SUDO_PASS=${var.vm_password_plain}"
    ]
    inline = [
      "while [ ! -f /var/lib/cloud/instance/boot-finished ]; do echo 'Waiting for cloud-init...'; sleep 1; done",
      "echo \"$SUDO_PASS\" | sudo -S rm /etc/ssh/ssh_host_*",
      "echo \"$SUDO_PASS\" | sudo -S truncate -s 0 /etc/machine-id",
      "echo \"$SUDO_PASS\" | sudo -S apt-get --assume-yes autoremove --purge",
      "echo \"$SUDO_PASS\" | sudo -S apt-get --assume-yes clean",
      "echo \"$SUDO_PASS\" | sudo -S apt-get --assume-yes autoclean",
      "echo \"$SUDO_PASS\" | sudo -S cloud-init clean",
      "echo \"$SUDO_PASS\" | sudo -S rm -f /etc/cloud/cloud.cfg.d/subiquity-disable-cloudinit-networking.cfg",
      "echo \"$SUDO_PASS\" | sudo -S sync",
      "echo \"$SUDO_PASS\" | sudo -S rm -rf /etc/netplan/50-cloud-init.yaml",
      "echo \"$SUDO_PASS\" | sudo -S cp /tmp/failover-netplan.yaml /etc/netplan/01-failover-netplan.yaml.disabled",
      "echo \"$SUDO_PASS\" | sudo -S cp /tmp/99-pve.cfg /etc/cloud/cloud.cfg.d/99-pve.cfg",
      "echo \"$SUDO_PASS\" | sudo -S cp /tmp/40_setup-serial-access.cfg /etc/default/grub.d/40_setup-serial-access.cfg",
      "echo \"$SUDO_PASS\" | sudo -S cat /etc/default/grub.d/40_setup-serial-access.cfg"#,
      #"echo \"$SUDO_PASS\" | sudo -S update-grub"
      ]
  }
}