import { callbacks, context, download, RepositoryType, untar } from '@pipeline/core';
import { shell } from '@pipeline/process';

(async function() {
  const filename = `${context.internal.binariesDirectory}/maven.tar.gz`;
  await download({
    sourcePath: 'java/maven/apache-maven-3.9.0-bin.tar.gz',
    targetPath: filename,
    context: context,
    type: RepositoryType.System
  });
  await untar(filename, context.internal.binariesDirectory);
  callbacks.addToPath(`${context.internal.binariesDirectory}/apache-maven-3.9.0/bin`);
})();
