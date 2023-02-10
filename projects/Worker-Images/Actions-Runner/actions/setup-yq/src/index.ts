import { callbacks, context, download, RepositoryType, untar } from '@pipeline/core';
import { shell } from '@pipeline/process';

(async function() {
  const filename = `${context.internal.binariesDirectory}/yq.tar.gz`;
  await download({
    sourcePath: 'misc/yq_linux_amd64.tar.gz',
    targetPath: filename,
    context: context,
    type: RepositoryType.System
  });
  const binpath = `${context.internal.binariesDirectory}/yq`;
  await shell(`mkdir -p ${binpath}`);
  await untar(filename, binpath);
  await shell(`rm ${filename} && mv ${binpath}/yq_linux_amd64 ${binpath}/yq`);
  callbacks.addToPath(`${binpath}`);
})();