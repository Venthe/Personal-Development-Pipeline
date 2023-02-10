import { context, RepositoryType, step, upload } from '@pipeline/core';
import { throwThis } from '@pipeline/utilities';
import { ActionStepDefinition } from '@pipeline/types';

(async function() {
    const stepDefinition = step as ActionStepDefinition<{ sourcePath: string, targetPath?: string }>;

    // TODO: Debug in actions should not show
    //console.debug(step, stepDefinition, stepDefinition.with, stepDefinition.with?.path)
    await upload({
        sourcePath: stepDefinition.with?.sourcePath ?? throwThis("Source path should be present"),
        targetPath: stepDefinition.with?.targetPath,
        context: context,
        type: RepositoryType.User
    });
})()