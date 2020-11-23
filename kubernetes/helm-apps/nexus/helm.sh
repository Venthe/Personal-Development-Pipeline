# kubectl delete namespace nexus

# helm repo add oteemocharts https://oteemo.github.io/charts
# helm pull oteemocharts/sonatype-nexus
helm upgrade \
  --create-namespace \
  --namespace=nexus \
  --install \
  --values=values.yml \
  nexus \
  oteemocharts/sonatype-nexus