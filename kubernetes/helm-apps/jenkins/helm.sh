# kubectl delete namespace jenkins

# helm repo add jenkins https://charts.jenkins.io
# helm pull jenkins/jenkins
helm upgrade \
  --create-namespace \
  --namespace=jenkins \
  --values=./values.yaml \
  --install \
  jenkins \
  jenkins/jenkins

# printf $(kubectl get secret --namespace jenkins jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo

# Jenkins.instance.pluginManager.plugins.each{
#   plugin ->
#     if(plugin.getDependents().size()==0)
#       println ("${plugin.getShortName()}:${plugin.getVersion()}")
# }