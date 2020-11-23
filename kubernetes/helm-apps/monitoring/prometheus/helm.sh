# helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
# helm pull prometheus-community/prometheus
helm upgrade \
  --install \
  --namespace=monitoring \
  --values=./values.yaml \
  prometheus \
  prometheus-community/prometheus
