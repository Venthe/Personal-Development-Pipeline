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
        [plugins.cri.registry.mirrors."docker.io"]
          endpoint = ["http://dockerhub-mirror.example.org"]
        [plugins.cri.registry.mirrors."*"]
          endpoint = ["http://dockerhub-mirror.example.org"]
        [plugins.cri.registry.mirrors."docker.example.org"]
          endpoint = ["http://docker.example.org"]

sudo systemctl restart containerd
```