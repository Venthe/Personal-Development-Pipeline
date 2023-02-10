import { callbacks, context, download, RepositoryType, untar } from '@pipeline/core';
import { shell } from '@pipeline/process';

async function setup() {
  const filename = `${context.internal.binariesDirectory}/helm.tar.gz`;
  await download({
    sourcePath: 'kubernetes/helm-v3.11.0-linux-amd64.tar.gz',
    targetPath: filename,
    context: context,
    type: RepositoryType.System
  });
  const binpath = `${context.internal.binariesDirectory}/helm`;
  await shell(`mkdir -p ${binpath}`);
  await untar(filename, binpath);
  await shell(`ls ${filename}`);
  await shell(`rm ${filename} && mv ${binpath}/linux-amd64/helm ${binpath}/helm`);
  callbacks.addToPath(`${binpath}`);
}

setup();
