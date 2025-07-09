variable "inventory_path" {
  type = string
}

variable "proxmox_url" {
  type    = string
  default = "https://proxmox.local:8006/api2/json"
}

variable "proxmox_user" {
  type    = string
  default = "root@pam"
}

variable "proxmox_node" {
  type    = string
  default = "proxmox-1"
}

variable "proxmox_vm_name" {
  type    = string
  default = "ubuntu"
}

variable "proxmox_password" {
  type      = string
  sensitive = true
}

variable "vm_password_hashed" {
  type      = string
  sensitive = true
}

variable "proxmox_storage_pool" {
  type = string
}

variable "proxmox_storage_pool_directory" {
  type = string
}

variable "vm_username" {
  type    = string
  default = "ubuntu"
}

variable "vm_gateway" {
  type = string
}

variable "vm_ssh_pubkeys" {
  type      = list(string)
  sensitive = true
  default   = []
}

terraform {
  required_providers {
    proxmox = {
      source  = "telmate/proxmox"
      version = "3.0.2-rc01"
    }
  }
}

provider "proxmox" {
  pm_api_url      = var.proxmox_url
  pm_user         = var.proxmox_user
  pm_password     = var.proxmox_password
  pm_tls_insecure = true
}

locals {
  inventory_path = "${path.module}/../inventory.yml"
  inventory_list = yamldecode(data.local_file.inventory.content)
  inventory      = { for vm in local.inventory_list : vm.vm-name => vm }
}

data "local_file" "inventory" {
  filename = var.inventory_path
}

resource "proxmox_cloud_init_disk" "ci" {
  for_each = { for vm in local.inventory : vm["vm-name"] => vm }
  name     = each.value["vm-name"]
  pve_node = var.proxmox_node
  storage  = var.proxmox_storage_pool_directory

  meta_data = yamlencode({
    instance_id    = sha1("${each.value["vm-name"]}")
    local-hostname = "${each.value["vm-name"]}"
  })

  user_data = templatefile("files/user-data.yaml.tpl", {
      vm_username        = var.vm_username
      vm_password_hashed = var.vm_password_hashed
      vm_ssh_pubkeys     = var.vm_ssh_pubkeys
    })

  network_config = yamlencode({
    version = 1
    config = [{
      type = "physical"
      name = "ens18"
      subnets = [{
        type    = "static"
        address = "${each.value["address"]}"
        gateway = "${var.vm_gateway}"
        dns_nameservers = [
          "1.1.1.1",
          "8.8.8.8"
        ]
      }]
    }]
  })
}

resource "proxmox_vm_qemu" "vm" {
  for_each    = { for vm in local.inventory : vm["vm-name"] => vm }
  name        = each.value["vm-name"]
  target_node = var.proxmox_node
  # FIXME: Add static IP configuration
  ipconfig0 = "ip=${each.value["address"]},gw=${var.vm_gateway}"

  clone      = "Ubuntu-25.04-server"
  full_clone = true

  agent = 1

  cpu {
    cores = 3
  }

  skip_ipv6 = true

  memory  = 20000
  balloon = 1

  boot   = "order=scsi0;net0"
  scsihw = "virtio-scsi-single"

  network {
    id       = 0
    model    = "virtio"
    bridge   = "vmbr0"
    firewall = true
  }

  disk {
    size    = "40G"
    slot    = "scsi0"
    type    = "disk"
    storage = var.proxmox_storage_pool
  }

  disk {
    slot = "scsi1"
    type = "cdrom"
    iso  = proxmox_cloud_init_disk.ci[each.key].id
  }
}
