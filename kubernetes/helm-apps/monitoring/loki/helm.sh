# helm repo add loki https://grafana.github.io/loki/charts
# helm pull loki/loki
helm upgrade \
  --install \
  --force \
  --namespace=monitoring \
  --set=service.type=LoadBalancer \
  --set=service.annotations."external-dns\.alpha\.kubernetes\.io/hostname"="loki.local" \
  loki \
  loki/loki
