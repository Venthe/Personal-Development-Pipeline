package eu.venthe.jenkins.actions

sh script: """\
GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git clone --depth 1 ${params.GIT_URL}/${params.PROJECT_NAME} ./
""", label: stepName

return [defaultRun: "", status: "ok"]
