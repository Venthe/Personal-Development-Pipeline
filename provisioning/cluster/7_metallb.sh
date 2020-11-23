#!/usr/bin/env bash

# If you’re using kube-proxy in IPVS mode, since Kubernetes v1.14.2 you have to enable strict ARP mode.
# see what changes would be made, returns nonzero returncode if different
# kubectl get configmap kube-proxy -n kube-system -o yaml \
#   | sed -e "s/strictARP: false/strictARP: true/" \
#   | kubectl diff -n kube-system -f -

# actually apply the changes, returns nonzero returncode on errors only
kubectl get configmap kube-proxy -n kube-system -o yaml \
   | sed -e "s/strictARP: false/strictARP: true/" \
   | kubectl apply -n kube-system -f -

kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.9.5/manifests/namespace.yaml
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.9.5/manifests/metallb.yaml
# On first install only
kubectl create secret generic -n metallb-system memberlist --from-literal=secretkey="$(openssl rand -base64 128)"

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  namespace: metallb-system
  name: config
data:
  config: |
    address-pools:
    - name: default
      protocol: layer2
      addresses:
      - 192.168.4.1-192.168.5.254
EOF