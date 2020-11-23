# helm repo add grafana https://grafana.github.io/helm-charts
# helm pull grafana/grafana
helm upgrade \
  --install \
  --namespace=monitoring \
  --values=./values.yaml \
  grafana \
  grafana/grafana
# kubectl get secret --namespace monitoring grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo