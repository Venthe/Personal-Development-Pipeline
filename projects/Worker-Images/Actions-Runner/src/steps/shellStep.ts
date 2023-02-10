import { ShellStepDefinition } from '@pipeline/types';
import { StepRunner } from './stepRunner';
import { ContextManager } from '../context/contextManager';
import { shell, ShellOutput } from '@pipeline/process';
import { getId as gid, Step } from './step';
import { renderTemplate } from '../utilities/template';
import { exceptionMapper, loadEnvironmentFile } from '../utilities';
import { error, throwThis } from '@pipeline/utilities';
import { ActionResult } from './actions';

export class ShellStep implements Step<ShellStepDefinition> {
  constructor(private readonly step: ShellStepDefinition,  private readonly index: number) {
  }

  get name(): string {
    return this.step.name ?? this.step.id ?? 'Shell';
  }

  async run(parentStepRunner: StepRunner,
            contextManager: ContextManager): Promise<ActionResult> {
    const tempFile = ((await shell('mktemp', { silent: true }))?.output?.[0].line || undefined) ?? throwThis(('Temp file should be created'));

    // TODO: Set current step env variables
    process.env['ACTION_OUTPUT'] = tempFile;

    const workingDirectoryOption = this.step.workingDirectory ? { cwd: this.step.workingDirectory } : {};
    const shellOption = this.step.shell ? { shell: this.step.shell } : {};
    try {
      const result: ShellOutput = await shell(renderTemplate(this.step.run, contextManager.contextSnapshot), { ...shellOption, ...workingDirectoryOption });
      console.debug(result);

      // TODO: Add to composite step/job outputs
      const outputs = loadEnvironmentFile<{ [outputId: string]: string }>(tempFile);
      console.debug(outputs);
      await shell(`rm -rf ${tempFile}`, { silent: true });
      process.env['ACTION_OUTPUT'] = undefined;

      return {
        outputs,
        outcome: result.code === 0 ? 'success' : 'failure'
      };
    } catch (err) {
      console.error(error(exceptionMapper(err)));
      return { outcome: 'failure' };
    }
  }

  get id(): string {
    return gid(this.step, this.index);
  }

  get if(): string | undefined {
    return this.step.if
  }
}