# kubectl delete namespace traefik

# helm repo add bitnami https://charts.bitnami.com/bitnami
# helm pull bitnami/metallb
helm upgrade \
  --create-namespace \
  --namespace=metallb \
  --values=./values.yaml \
  --install \
  metallb  \
  bitnami/metallb
  