# kubectl get pods --all-namespaces | grep gerrit | grep deployment | awk '{print $2}'
# kubectl exec --namespace=gerrit-namespace $(kubectl get pods --all-namespaces | grep gerrit | grep deployment | awk '{print $2}') "gerrit create-project jenkins"
#  kubectl  exec --namespace=gerrit-namespace svc/gerrit-service -- ssh -p 29418 -o "StrictHostKeyChecking no" jjuly@localhost gerrit create-project common/jenkins.git
ssh -p 29418 -o "StrictHostKeyChecking no" jjuly@gerrit.local gerrit create-project --branch=main Common/Personal-Development-Pipeline.git
git push ssh://jjuly@gerrit.local:29418/Common/Personal-Development-Pipeline HEAD:refs/heads/main --force

# ssh -p 29418 -o "StrictHostKeyChecking no" jjuly@gerrit.local gerrit create-group --description "'Non-Interactive Users'" "Non Interactive Users"