# Jenkins

```groovy
Jenkins.instance.pluginManager.plugins.each{
  plugin ->
    if(plugin.getDependents().size()==0)
      println ("${plugin.getShortName()}:${plugin.getVersion()}")
}
```

```bash
kubectl get secret \
    --namespace jenkins \
    jenkins \
    --output=jsonpath="{.data.jenkins-admin-password}" \
    | base64 --decode
```

```bash
# Create this from combined ca certifications from node
kubectl create configmap ca-certificates.crt --namespace=jenkins --from-file=ca-certificates.crt=./configuration/ca-certificates.crt
```