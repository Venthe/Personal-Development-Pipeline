#!/usr/bin/env sh

set -x

echo "Preparing SSH keys"
mkdir ~/.ssh
cp -r ~/workdir_ssh/* ~/.ssh
chown -R root ~/.ssh
chmod 600 ~/.ssh/id_rsa

# git config --global user.name "${GIT_MAIL}"
# git config --global user.email "${GIT_USERNAME}"
# git config --global core.editor vim
# git config --global core.pager less
# git config --global help.autocorrect 5
# git config --global color.ui true
# git config --global core.autocrlf true
# git config --global diff.renameLimit 65553
# git config --global diff.renames true
# git config --global diff.algorithm histogram
# git config --global feature.manyFiles true
git config --global init.defaultBranch main

echo "Preparing repository"
cp -r /workdir/* /workcopy
cd /workcopy
git init
git add --all
git commit -m "Initial commit"

echo "Creating project in gerrit"
ssh \
    -v \
    -p 29418 \
    -o "StrictHostKeyChecking no" \
    ${GERRIT_USERNAME}@${GERRIT_URL} \
    gerrit create-project \
        --branch=${GERRIT_BRANCH} \
        "${PROJECT_NAME}.git" \
    -i ~/.ssh/id_rsa
echo "Pushing"
git push ssh://${GERRIT_USERNAME}@${GERRIT_URL}:29418/${PROJECT_NAME} HEAD:refs/heads/${GERRIT_BRANCH} --force
