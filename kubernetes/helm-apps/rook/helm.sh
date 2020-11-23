# kubectl delete namespace rook-ceph
# kubectl create namespace rook-ceph

# helm repo add rook-release https://charts.rook.io/release
# helm pull rook-release/rook-ceph
helm install \
  rook-ceph \
  rook-release/rook-ceph \
  --namespace=rook-ceph
  
kubectl create -f https://raw.githubusercontent.com/rook/rook/v1.4.6/cluster/examples/kubernetes/ceph/cluster.yaml

# https://rook-ceph-dashboard.local:8443/#/login
# kubectl -n rook-ceph get secret rook-ceph-dashboard-password -o jsonpath="{['data']['password']}" | base64 --decode && echo
kubectl create -f https://raw.githubusercontent.com/rook/rook/v1.4.6/cluster/examples/kubernetes/ceph/dashboard-loadbalancer.yaml
kubectl annotate service rook-ceph-mgr-dashboard-loadbalancer \
  --namespace=rook-ceph \
  --overwrite \
  external-dns.alpha.kubernetes.io/hostname="rook-ceph-dashboard.local"

# kubectl -n rook-ceph exec -it $(kubectl -n rook-ceph get pod -l "app=rook-ceph-tools" -o jsonpath='{.items[0].metadata.name}') bash
kubectl create -f https://raw.githubusercontent.com/rook/rook/v1.4.6/cluster/examples/kubernetes/ceph/toolbox.yaml

kubectl apply -f ./rook-ceph-block.yml
kubectl annotate StorageClass rook-ceph-block \
  --namespace=rook-ceph \
  --overwrite \
  storageclass.kubernetes.io/is-default-class: "true"

kubectl apply -f ./rook-ceph-shared-filesystem.yml
kubectl annotate StorageClass rook-cephfs \
  --namespace=rook-ceph \
  --overwrite \
  storageclass.kubernetes.io/is-default-class: "false"
