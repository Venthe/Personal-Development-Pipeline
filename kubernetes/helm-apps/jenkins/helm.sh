# kubectl delete namespace jenkins

helm repo add jenkins https://charts.jenkins.io
helm repo update
# helm pull jenkins/jenkins
helm upgrade --install \
  --create-namespace --namespace=jenkins \
  --values=./values.yaml \
  jenkins \
  jenkins/jenkins

# printf $(kubectl get secret --namespace jenkins jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo

# Jenkins.instance.pluginManager.plugins.each{
#   plugin ->
#     if(plugin.getDependents().size()==0)
#       println ("${plugin.getShortName()}:${plugin.getVersion()}")
# }