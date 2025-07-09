#cloud-config
users:
  - name: $vm_username
    %{ if length(vm_ssh_pubkeys) > 0 }
    authorized-keys:
      %{ for key in vm_ssh_pubkeys ~}
      - ${key}
      %{ endfor }
    %{ else }
    authorized-keys: []
    %{ endif }
    shell: /bin/bash
    passwd: $vm_password_hashed
    groups: [sudo]
disable_root: false
ssh_pwauth: true
# runcmd:
#   - lvextend -l +100%FREE /dev/mapper/ubuntu--vg-ubuntu--lv
#   - resize2fs /dev/mapper/ubuntu--vg-ubuntu--lv