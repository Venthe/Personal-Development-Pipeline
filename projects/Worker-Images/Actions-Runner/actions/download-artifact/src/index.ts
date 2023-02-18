import { context, download, RepositoryType, step } from '@pipeline/core';
import { throwThis } from '@pipeline/utilities';
import { ActionStepDefinition } from '@pipeline/types';

(async function() {
  const step1 = step as ActionStepDefinition<{ sourcePath: string, targetPath?: string }>
  await download({
    sourcePath: step1.with?.sourcePath ?? throwThis('Source path should be present'),
    targetPath: (step1.with?.targetPath ?? step1.with?.sourcePath?.split('/').pop()) || '',
    context: context,
    type: RepositoryType.User
  });
})();