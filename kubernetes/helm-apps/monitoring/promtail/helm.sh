# helm repo add loki https://grafana.github.io/loki/charts
# helm pull loki/promtail
helm upgrade \
  --install \
  --force \
  --namespace=monitoring \
  --set=service.type=LoadBalancer \
  --set=service.annotations."external-dns\.alpha\.kubernetes\.io/hostname"="promtail.local" \
  promtail \
  loki/promtail --set "loki.serviceName=loki"
