# helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
# helm pull ingress-nginx/ingress-nginx
helm upgrade \
  --install \
  --values=./values.yml \
  ingress-nginx \
  ingress-nginx/ingress-nginx

#   apiVersion: networking.k8s.io/v1beta1
#   kind: Ingress
#   metadata:
#     annotations:
#       kubernetes.io/ingress.class: nginx
#     name: example
#     namespace: foo
#   spec:
#     rules:
#       - host: www.example.com
#         http:
#           paths:
#             - backend:
#                 serviceName: exampleService
#                 servicePort: 80
#               path: /
#     # This section is only required if TLS is to be enabled for the Ingress
#     tls:
#         - hosts:
#             - www.example.com
#           secretName: example-tls

# If TLS is enabled for the Ingress, a Secret containing the certificate and key must also be provided:

#   apiVersion: v1
#   kind: Secret
#   metadata:
#     name: example-tls
#     namespace: foo
#   data:
#     tls.crt: <base64 encoded cert>
#     tls.key: <base64 encoded key>
#   type: kubernetes.io/tls