# kubectl delete namespace sonarqube

# helm repo add oteemocharts https://oteemo.github.io/charts
# helm pull oteemocharts/sonarqube
helm install \
  --namespace=sonarqube \
  --name=sonarqube \
  oteemocharts/sonarqube
  