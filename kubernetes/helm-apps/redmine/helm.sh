# kubectl delete namespace redmine
# kubectl create namespace redmine

helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
# helm pull bitnami/redmine
helm upgrade --install \
  --namespace=redmine --create-namespace \
  --values=./values.yml \
  redmine \
  bitnami/redmine

cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: redmine-nginx
  namespace: redmine
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: redmine.example.org
    http:
      paths:
      - backend:
          serviceName: redmine
          servicePort: 80
EOF
