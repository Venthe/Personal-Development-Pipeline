#/usr/bin/bash

SERVER_HOST="192.168.2.76"

# function execute() {
#     kubectl exec --stdin --tty --namespace=gerrit gerrit-gerrit-stateful-set-0 -- sh -c "${1}"
# }

# execute "rm -rf /tmp/All-Users"
# execute "cd /tmp && git clone /var/gerrit/git/All-Users.git/"
# execute "cd /tmp/All-Users && git fetch origin +refs/users/*:refs/remotes/origin/refs/users/*"
# execute "cd /tmp/All-Users && git checkout -b admin origin/\$(git ls-remote origin | grep users/ | awk '{print \$2}' | head -n 1)"
# PUBLIC_KEY=$(cat ~/.ssh/id_rsa.pub)
# execute "cd /tmp/All-Users && printf \"${PUBLIC_KEY}\" > authorized_keys"
# execute "cd /tmp/All-Users && git add authorized_keys"
# execute "cd /tmp/All-Users && git commit -m \"Updated SSH keys\""
# execute "cd /tmp/All-Users && git push origin HEAD:\$(git ls-remote origin | grep users/ | awk '{print \$2}' | head -n 1)"

# # Generate HTTP password
# SSH_PORT=$(kubectl get services -n gerrit -o=jsonpath='{.items[*].spec.ports[?(@.name=="ssh")].nodePort}')
# HTTP_PASSWORD=$(ssh -p ${SSH_PORT} \
#     admin@${SERVER_HOST} \
#     gerrit set-account admin --generate-http-password \
#     | awk '{print $3}' \
#     | head -n1)

./provision.sh rest GET a/accounts/self
rest PUT a/accounts/self/status -H "Content-Type: application/json" -d '{
"status": "Out of Office"
}'
./provision.sh rest PUT a/projects/All-Projects/labels/Verified -H "Content-Type: application/json" -d '{
    "commit_message": "Create verified label",
    "values": {
      " 0": "No score",
      "-1": "I would prefer this is not merged as is",
      "-2": "This shall not be merged",
      "+1": "Looks good to me, but someone else must approve",
      "+2": "Looks good to me, approved"
    }
}'

./provision.sh rest PUT a/projects/Jenkins --header "Content-Type: application/json" --data '{
    "description": "This is base Jenkins project",
    "submit_type": "INHERIT"
  }
'

./provision.sh rest PUT a/projects/Sample-Jenkins-Git-Repository --header "Content-Type: application/json" --data '{
    "description": "This is sample project",
    "submit_type": "INHERIT"
  }
'

# ,
#     "owners": [
#       "MyProject-Owners"
#     ]