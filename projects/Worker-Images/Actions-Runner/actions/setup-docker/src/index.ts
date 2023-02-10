import { callbacks, context } from '@pipeline/core';

(async function() {
  // We usually ned not to setup this via apt
  if (context.internal.dockerHost) callbacks.addEnv('DOCKER_HOST', context.internal.dockerHost);
})();