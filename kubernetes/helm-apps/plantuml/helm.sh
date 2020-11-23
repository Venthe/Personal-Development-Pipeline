# kubectl delete namespace plantuml
# kubectl create namespace plantuml

# helm repo add gitlab https://charts.gitlab.io/
# helm pull gitlab/plantuml
helm install \
  --namespace=plantuml \
  --name=plantuml \
  gitlab/plantuml
  