kubectl get \
    --namespace=kube-system \
    --output=jsonpath='{.data.Corefile}' \
    configmap/coredns
echo "# START BLOCK MANAGED BY SHELL: DNS_REDIRECT
{{ tld_hostname }}:53 {
    errors
    cache 30
    forward . $(kubectl get service \
      --namespace="{{ externaldns.namespace }}" \
      --output=jsonpath='{.status.loadBalancer.ingress[0].ip}' \
      external-dns-public)
}
# START BLOCK MANAGED BY SHELL: DNS_REDIRECT"
# >> /tmp/coredns/Corefile
# kubectl patch \
#     --namespace=kube-system \
#     configmap/coredns \
#     --patch="{\"data\":{\"Corefile\":\"$(cat /tmp/coredns/Corefile | awk -v ORS='\\n' '1')\"}}"