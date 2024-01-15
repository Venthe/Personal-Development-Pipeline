#!/usr/bin/env bash

mkdir ./gerrit
cd gerrit

git clone http://gerrit/a/All-Projects
git fetch origin refs/meta/config:refs/remotes/origin/meta/config
git checkout meta/config
cp ../webhooks.config ./
sed "s/$PIPELINE_URL/${1}/g"
git add webhooks.config
git commit -m "Add webhook configuration"
git push origin meta/config:refs/meta/config
