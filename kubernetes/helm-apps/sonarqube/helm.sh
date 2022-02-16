# kubectl delete namespace sonarqube

helm repo add oteemocharts https://oteemo.github.io/charts
# helm pull oteemocharts/sonarqube
helm repo update
helm upgrade --install \
  --namespace=sonarqube --create-namespace \
  sonarqube \
  oteemocharts/sonarqube

cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: sonarqube-nginx
  namespace: sonarqube
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: sonarqube.home.arpa
    http:
      paths:
      - backend:
          service:
            name: sonarqube-sonarqube
            port: 9000
        pathType: ImplementationSpecific
EOF