import { context } from '@pipeline/core';
import { shellMany } from '@pipeline/process';

(async () => {
  const gerritURL = context.internal.gerritUrl;
  const event: any = context.internal.event;
  await shellMany([
    `git init`,
    `git fetch --tags --force --progress --depth=1 -- ${gerritURL}/${event.change.project} '+refs/heads/*:refs/remotes/origin/!*'`,
    `git config remote.origin.url ${gerritURL}/${event.change.project}`,
    `git config --add remote.origin.fetch '+refs/heads/!*:refs/remotes/origin/!*'`,
    `git config remote.origin.url ${gerritURL}/${event.change.project}`,
    `git fetch --tags --force --progress --depth=1 -- ${gerritURL}/${event.change.project} '${event.patchSet.ref}'`,
    `git checkout -f FETCH_HEAD -b change-${event.change.number}`
  ]);
})();