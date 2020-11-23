# kubectl delete namespace xwiki
# kubectl create namespace xwiki

# git clone https://github.com/xwiki-contrib/xwiki-helm
cd xwiki-helm
helm dependency update
helm install \
  --values=./values.yaml \
  --namespace=xwiki
  --name=xwiki \
  .