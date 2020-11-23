# kubectl delete namespace dashboard
# kubectl create namespace dashboard

# helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
# helm pull kubernetes-dashboard/kubernetes-dashboard
helm install \
  dashboard \
  kubernetes-dashboard/kubernetes-dashboard \
  --namespace=dashboard \
  --values=./values.yaml
# kubectl apply -f=./user.yml
kubectl get secret $(kubectl get secret --namespace=dashboard --output=jsonpath="{range .items[*]}{.metadata.name}{'\n'}{end}" | grep admin-user) \
        --namespace=dashboard \
        --output=jsonpath="{.data.token}" |
        awk '{system("echo -n "$1" | base64 --decode")}'