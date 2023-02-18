import * as process from 'process';
import { WorkflowOrchestrator } from './workflow/workflowOrchestrator';
import { PipelineEnvironmentVariables } from './configuration/environment';
import { configureGit, exceptionMapper, saveObjectAsFile } from './utilities';
import { error } from '@pipeline/utilities';

export const main = async () => {
  try {
    let env = process.env as PipelineEnvironmentVariables;

    await configureGit();
    const workflowOrchestrator = await WorkflowOrchestrator.create(env);
    const result = await workflowOrchestrator.run();

    saveObjectAsFile('/runner/result.json', result);
    if (result.result === 'failure') {
      process.exit(1)
    }
  } catch (exception: any) {
    console.error(error(`Unhandled fatal exception: ${exceptionMapper(exception)}`));
    throw (exception instanceof Error ? exception : new Error(JSON.stringify(exception)));
  }
};
