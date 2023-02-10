import { callbacks, context, download, RepositoryType, untar } from '@pipeline/core';

(async function() {
  const filename = `${context.internal.binariesDirectory}/java.tar.gz`;
  await download({
    sourcePath: 'java/jdk/zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz',
    targetPath: filename,
    context: context,
    type: RepositoryType.System
  });
  await untar(filename, context.internal.binariesDirectory);
  callbacks.addToPath(`${context.internal.binariesDirectory}/zulu17.38.21-ca-jdk17.0.5-linux_x64/bin`);
  callbacks.addEnv('JAVA_HOME', `${context.internal.binariesDirectory}/zulu17.38.21-ca-jdk17.0.5-linux_x64`);
})();