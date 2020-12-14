#/usr/bin/bash 

kubectl exec --stdin --tty --namespace=gerrit gerrit-gerrit-stateful-set-0 -- sh
cd /tmp
# Clone all users
git clone /var/gerrit/git/All-Users.git/
cd All-Users
# Assume that admin was the one created first ;)
ADMIN_BRANCH=$(git ls-remote origin | grep users/ | awk '{print $2}' | head -n 1)
git fetch origin +refs/users/*:refs/remotes/origin/refs/users/*
git checkout -b admin origin/${ADMIN_BRANCH}
cat <<EOF > authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDCP+paJSav/ujCKxILhljBgBk+TnqGaO9HqY62bID3FIdeiKa+tWg5e0w5hKqIJbSFfro7OGT3ZZSOv1J9ou9wwsDo5+UxMNJO3jI9m9PEW9xQOvLq9GvEgQBg/L2jYyNwkaKrBEGlqKGw0pnMwdzNcE6uyKyDMq840RLOR9kYa7Qm6igXNzdNydwWdTa0OwxlIxFSp8SADqVwUMJnq9NswRN/Xw6be3BsCWnmNvAHgMlJxnKC9I1AGjMKti+4F6qQvYOSIaPtnxfHx6TvDrbRYBk8A9/GX4b/7YFbOr1m8IAVIDf+GYiQ9SVXxaS5RkRQ4AzTenaNnF9vsOWmQ1TN jacek.lipiec.bc@gmail.com
EOF
git add authorized_keys
git commit -m "Updated SSH keys"
git push origin HEAD:${ADMIN_BRANCH}
exit

# Generate HTTP password
SSH_PORT=$(kubectl get services -n gerrit -o=jsonpath='{.items[*].spec.ports[?(@.name=="ssh")].nodePort}')
HTTP_PASSWORD=$(ssh -p ${SSH_PORT} \
    admin@localhost \
    gerrit set-account admin --generate-http-password \
    | awk '{print $3}' \
    | head -n1)

# From this point on we can use CURL
HTTP_PORT=$(kubectl get services -n gerrit -o=jsonpath='{.items[*].spec.ports[?(@.name=="http")].nodePort}')
HTTP_BASIC_AUTH=$(printf admin:${HTTP_PASSWORD} | base64)

function rest() {
    local method="${1}"
    local path="${2}"
    shift 2
    echo ${path}
    curl -X "${method}" admin:${HTTP_PASSWORD}@localhost:${HTTP_PORT}/a/"${path}" "${@}"
}
rest GET accounts/self
rest PUT accounts/self/status -H "Content-Type: application/json" -d '{
"status": "Out of Office"
}'
rest PUT projects/All-Projects/labels/Verified -H "Content-Type: application/json" -d '{
    "commit_message": "Create verified label",
    "values": {
      " 0": "No score",
      "-1": "I would prefer this is not merged as is",
      "-2": "This shall not be merged",
      "+1": "Looks good to me, but someone else must approve",
      "+2": "Looks good to me, approved"
    }
}'
