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
