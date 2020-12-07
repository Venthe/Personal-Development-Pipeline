helm repo add gitlab https://charts.gitlab.io/
# helm pull gitlab/plantuml
helm upgrade --install \
  --namespace=plantuml --create-namespace \
  --set "nginx_sidecar.server_name=plantuml.example.org" \
  plantuml \
  gitlab/plantuml

cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: plantuml-nginx
  namespace: plantuml
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: plantuml.example.org
    http:
      paths:
      - backend:
          serviceName: plantuml
          servicePort: 80
EOF
