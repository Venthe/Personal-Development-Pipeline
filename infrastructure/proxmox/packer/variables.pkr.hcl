variable "vm_hostname" {
  type    = string
  default = "ubuntu-host"
}

variable "vm_username" {
  type    = string
  default = "ubuntu"
}

variable "vm_password_plain" {
  type      = string
  sensitive = true
}

# Generate with: mkpasswd --method=SHA-512
variable "vm_password_hashed" {
  type      = string
  sensitive = true
}

variable "vm_keyboard_layout" {
  type    = string
  default = "pl"
}

variable "vm_keyboard_variant" {
  type    = string
  default = ""
}

variable "vm_locale" {
  type    = string
  default = "en_US.UTF-8"
}

variable "vm_ip_addresses" {
  type    = list(string)
  default = ["192.168.1.100/24"]
}

variable "vm_gateway" {
  type    = string
  default = "192.168.1.1"
}

variable "vm_nameservers" {
  type    = list(string)
  default = ["1.1.1.1", "8.8.8.8"]
}

variable "vm_ssh_pubkeys" {
  type      = list(string)
  sensitive = true
  default   = []
}

variable "proxmox_url" {
  type    = string
  default = "https://proxmox.local:8006/api2/json"
}

variable "proxmox_storage_pool" {
  type = string
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