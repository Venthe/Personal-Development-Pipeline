kubectl create namespace external-dns
kubectl apply -f ./etcd-crd.yaml
kubectl apply -f ./cilium-etcd-operator.yaml
helm upgrade \
  --install \
  --namespace external-dns \
  --values coredns-values.yml \
  --version="1.13.6" \
  coredns \
  stable/coredns
helm upgrade \
  --install \
  --values "./external-dns-values.yaml" \
  --namespace external-dns \  
  external-dns \
  bitnami/external-dns

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Service
metadata:
  namespace: external-dns
  name: coredns-public
spec:
  type: LoadBalancer
  ports:
    - port: 53
      targetPort: 53
      protocol: TCP
      name: dns-tcp
    - port: 53
      targetPort: 53
      protocol: UDP
      name: dns-udp
  selector:
    app.kubernetes.io/name: coredns
EOF


















# # helm pull stable/coredns
# helm upgrade \
#   --set=customResources.createEtcdClusterCRD=true \
#   --install \
#   my-etcd-op \
#   stable/etcd-operator
# # 1. Watch etcd cluster start
# #   kubectl get pods -l etcd_cluster=etcd-cluster --namespace kube-system -w

# # 2. Confirm etcd cluster is healthy
# #   $ kubectl run --rm -i --tty --env="ETCDCTL_API=3" --env="ETCDCTL_ENDPOINTS=http://etcd-cluster-client:2379" --namespace kube-system etcd-test --image quay.io/coreos/etcd --restart=Never -- /bin/sh -c 'watch -n1 "etcdctl  member list"'

# # 3. Interact with the cluster!
# #   $ kubectl run --rm -i --tty --env ETCDCTL_API=3 --namespace kube-system etcd-test --image quay.io/coreos/etcd --restart=Never -- /bin/sh
# #   / # etcdctl --endpoints http://etcd-cluster-client:2379 put foo bar
# #   / # etcdctl --endpoints http://etcd-cluster-client:2379 get foo
# #   OK
# #   (ctrl-D to exit)

# # 4. Optional
# #   Check the etcd-operator logs
# #   export POD=$(kubectl get pods -l app=my-etcd-op-etcd-operator-etcd-operator --namespace kube-system --output name)
# #   kubectl logs $POD --namespace=kube-system



# # It can be tested with the following:

# # 1. Launch a Pod with DNS tools:

# # kubectl run -it --rm --restart=Never --image=infoblox/dnstools:latest dnstools

# # 2. Query the DNS server:

# # / # host kubernetes

# kubectl apply -f ./external-dns.yaml