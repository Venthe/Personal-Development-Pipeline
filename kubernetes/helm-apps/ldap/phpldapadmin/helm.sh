# helm repo add cetic https://cetic.github.io/helm-charts
# helm pull cetic/phpldapadmin
helm upgrade \
  phpldapadmin \
  cetic/phpldapadmin \
  --install \
  --values=./values.yml \
  --namespace=ldap
echo http://$(kubectl get svc --namespace=ldap phpldapadmin --output=jsonpath='{.metadata.annotations.external-dns\.alpha\.kubernetes\.io/hostname}')/