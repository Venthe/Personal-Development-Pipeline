# kubectl delete namespace gerrit

# git clone https://github.com/GerritCodeReview/k8s-gerrit.git
ORIGINAL_PWD=$(pwd)
cd k8s-gerrit

cd $(git rev-parse --show-toplevel)/helm-charts/gerrit
"/d/Tools/helm/helm-v3.3.4-windows-amd64/helm.exe" upgrade \
  --install \
  --create-namespace \
  --values="${ORIGINAL_PWD}/values.yaml" \
  --namespace=gerrit \
  gerrit \
  .

cd "${ORIGINAL_PWD}"
kubectl annotate service gerrit-gerrit-service \
  --namespace=gerrit \
  --overwrite \
  external-dns.alpha.kubernetes.io/hostname="gerrit.local"

# cd $(git rev-parse --show-toplevel)/helm-charts/gerrit-replica
# helm dependency update
# helm install \
#   --values=./values.yaml \
#   --namespace=gerrit
#   --name=gerrit-replica \
#   .