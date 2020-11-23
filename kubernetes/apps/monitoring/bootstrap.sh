function get() {
   local grep=$1
   cat ~/.kube/config \
        | grep -E "${grep}" \
        | awk  '{print $2}'
}

_CERTIFICATE_AUTHORITY_DATA=$(get "certificate-authority-data" | base64 --decode)
_CLIENT_KEY_DATA=$(get "client-key-data" | base64 --decode)
_CLIENT_CERTIFICATE_DATA=$(get "client-certificate-data" | base64 --decode)
_SERVER=$(get "server")

yq write --inplace "secrets/grafana/datasources.yaml" --style=literal datasources[0].secureJsonData.tlsClientCert -- "${_CLIENT_CERTIFICATE_DATA}"
yq write --inplace "secrets/grafana/datasources.yaml" --style=literal datasources[name==kubernetes-master-node].secureJsonData.tlsClientKey -- "${_CLIENT_KEY_DATA}"
yq write --inplace "secrets/grafana/datasources.yaml" --style=literal datasources[name==kubernetes-master-node].secureJsonData.tlsCACert -- "${_CERTIFICATE_AUTHORITY_DATA}"
yq write --inplace "secrets/grafana/datasources.yaml" datasources[name==kubernetes-master-node].url "${_SERVER}"
yq write --inplace "secrets/grafana/datasources.yaml" datasources[name==Prometheus].url "http://prometheus-service:80"
