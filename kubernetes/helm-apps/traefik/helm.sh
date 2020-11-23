# kubectl delete namespace traefik

# helm repo add traefik https://helm.traefik.io/traefik
# helm pull traefik/traefik
helm upgrade \
  --create-namespace \
  --namespace=traefik \
  --values=./values.yml \
  --install \
  traefik  \
  traefik/traefik

kubectl apply -f ./traefik-dashboard.yml