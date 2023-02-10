import { password as passwordRegExp, shell } from '@pipeline/process';
import { context, step } from '@pipeline/core';
import { ActionStepDefinition } from '@pipeline/types';

type Docker = { url?: string, username?: string, password?: string };

(async function() {

  const step1 = step as ActionStepDefinition<Docker>;
  const url = step1.with?.url || context.internal.dockerUrl;
  const username = step1.with?.username ?? context.secrets.DOCKER_USERNAME;
  const password = step1.with?.password ? context.secrets[step1.with?.password] : context.secrets.DOCKER_PASSWORD;
  await shell(`echo ${password} | docker login ${url ?? ''} --username ${username} --password-stdin`, { mask: [passwordRegExp('echo')] });
})();