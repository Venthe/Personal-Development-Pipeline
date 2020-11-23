# kubectl delete namespace ghost
# kubectl create namespace ghost

# helm repo add bitnami https://charts.bitnami.com/bitnami
# helm pull bitnami/ghost
helm install \
  --namespace=ghost \
  --name=ghost \
  bitnami/ghost
  