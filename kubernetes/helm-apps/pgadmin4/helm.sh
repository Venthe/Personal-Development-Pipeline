# kubectl delete namespace pgadmin
# kubectl create namespace pgadmin

# helm repo add runix https://helm.runix.net/
# helm pull bitnami/redmine
helm install \
  --namespace=pgadmin \
  --name=pgadmin4 \
  runix/pgadmin4
  