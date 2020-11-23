# kubectl delete namespace redmine
# kubectl create namespace redmine

# helm repo add bitnami https://charts.bitnami.com/bitnami
# helm pull bitnami/redmine
helm install \
  --namespace=redmine \
  --name=redmine \
  bitnami/redmine
  