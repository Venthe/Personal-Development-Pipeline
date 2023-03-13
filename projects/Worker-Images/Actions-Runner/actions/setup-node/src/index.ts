import { callbacks, context, download, RepositoryType, untar } from '@pipeline/core';

(async function() {
  const filename = `${context.internal.binariesDirectory}/node.tar.xz`;
  await download({
    sourcePath: 'node/node-v18.15.0-linux-x64.tar.xz',
    targetPath: filename,
    context: context,
    type: RepositoryType.System
  });
  await untar(filename, context.internal.binariesDirectory);
  callbacks.addToPath(`${context.internal.binariesDirectory}/node-v18.15.0-linux-x64/bin`);
})();
