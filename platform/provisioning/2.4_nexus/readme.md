# Setup nexus

# Setup externaldns to be used in linux nodes

# Setup containerd with public name

```/etc/containerd/config.toml```
```
# /etc/containerd/config.toml
[plugins]
  [plugins.cri]
    [plugins.cri.registry]
      [plugins.cri.registry.mirrors]
        [plugins.cri.registry.mirrors."*"]
          endpoint = ["http://dockerhub-mirror.home.arpa"]

sudo systemctl restart containerd
```