import { ContextSnapshot } from '@pipeline/types';
import { shell, shellMany } from '@pipeline/process';

type CheckoutParams = { project: string, revision: string, cwd: string, directory?: string, silent?: boolean };

export type CheckoutType = (params: CheckoutParams, context: ContextSnapshot) => Promise<void>;

export const checkout: CheckoutType = async ({ project, revision, cwd, directory, ...rest }, context) => {
  const gerritURL = context.internal.gerritUrl;
  console.debug(`Downloading from Gerrit: ${project}, ${revision}, ${cwd}`);

  const finalCwd = (cwd ?? process.cwd) + (directory ? `/${directory}` : '');
  await shell(`mkdir --parents ${finalCwd}`, { silent: rest.silent ?? false });
  await shellMany([
    `git init`,
    `git fetch --quiet --tags --force --progress --depth=1 -- ${gerritURL}/${project} '+refs/heads/*:refs/remotes/origin/*'`,
    `git config remote.origin.url ${gerritURL}/${project}`,
    `git config --add remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'`,
    `git config remote.origin.url ${gerritURL}/${project}`,
    `git fetch --quiet --tags --force --progress --depth=1 -- ${gerritURL}/${project} '${revision}'`,
    `git checkout -f FETCH_HEAD -b ${revision}`
  ], { cwd: finalCwd, ...{ silent: rest.silent ?? false } });
};