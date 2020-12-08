# kubectl delete namespace monitoring

helm repo add grafana https://grafana.github.io/helm-charts
helm repo add loki https://grafana.github.io/loki/charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm upgrade --install \
  --namespace=monitoring --create-namespace \
  --values=./grafana/values.yaml \
  grafana \
  grafana/grafana
# kubectl get secret --namespace monitoring grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo

helm upgrade --install \
  --namespace=monitoring --create-namespace \
  loki \
  loki/loki

helm upgrade --install \
  --namespace=monitoring --create-namespace \
  prometheus \
  prometheus-community/prometheus

helm upgrade --install \
  --namespace=monitoring --create-namespace \
  promtail \
  loki/promtail --set "loki.serviceName=loki"

cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: nginx
  namespace: monitoring
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: grafana.example.org
    http:
      paths:
      - backend:
          serviceName: grafana
          servicePort: 80
  - host: prometheus.example.org
    http:
      paths:
      - backend:
          serviceName: prometheus-server
          servicePort: 80
  - host: loki.example.org
    http:
      paths:
      - backend:
          serviceName: loki
          servicePort: 80
EOF
