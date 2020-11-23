# kubectl delete namespace phpmyadmin
# kubectl create namespace phpmyadmin

# helm repo add bitnami https://charts.bitnami.com/bitnami
# helm pull bitnami/phpmyadmin
helm install \
  --namespace=phpmyadmin \
  --name=phpmyadmin \
  bitnami/phpmyadmin
  