# helm repo add stable https://kubernetes-charts.storage.googleapis.com
# helm pull stable/openldap
helm upgrade \
  --create-namespace \
  --install \
  --namespace=ldap \
  --set-file customLdifFiles."01-default-users\.ldif"=export.ldif \
  --values=./values.yml \
  openldap \
  stable/openldap