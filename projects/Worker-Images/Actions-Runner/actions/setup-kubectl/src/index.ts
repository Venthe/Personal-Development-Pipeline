import { shell } from '@pipeline/process';
import { callbacks, context, download, RepositoryType } from '@pipeline/core';

(async function() {
  await shell(`mkdir -p ${context.internal.binariesDirectory}/kubectl`);
  await download({
    sourcePath: 'kubernetes/kubectl-linux-adm64-v1.26.0',
    targetPath: `${context.internal.binariesDirectory}/kubectl/kubectl`,
    context,
    type: RepositoryType.System
  });
  await shell(`chmod +x ${context.internal.binariesDirectory}/kubectl/kubectl`);
  callbacks.addToPath(`${context.internal.binariesDirectory}/kubectl`);
  await shell('kubectl config set-context --current --namespace=default');
})();