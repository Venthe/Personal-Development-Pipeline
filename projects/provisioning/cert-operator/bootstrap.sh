helm repo add cert-utils-operator https://redhat-cop.github.io/cert-utils-operator
helm repo update
helm install cert-utils-operator cert-utils-operator/cert-utils-operator