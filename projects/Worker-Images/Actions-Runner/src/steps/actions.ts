import {
  ActionStepDefinition,
  BaseActionDefinition,
  CompositeActionDefinition,
  ContextSnapshot,
  FinalStatus,
  NodeActionDefinition,
  StepOutputs
} from '@pipeline/types';
import {ContextManager} from '../context/contextManager';
import {AddEnvMessage, AddToPathMessage, checkout, SetOutputMessage} from '@pipeline/core';
import {loadYamlFile} from '../utilities';
import fs from 'fs';
import {throwThis} from '@pipeline/utilities';
import * as childProcess from 'child_process';
import {StepRunner, StepRunnerResult} from './stepRunner';

export interface ActionResult {
  outcome: FinalStatus,
  outputs?: StepOutputs
}

type LocalActionExpression = <T extends object = {}>(
  context: ContextSnapshot,
  callbacks: {
    addEnv: (key: string, value: any) => void,
    addToPath: (path: string) => void
  }
) => Promise<ActionResult>;

export class Action {
  private readonly uses: string;
  private action?: LocalActionExpression;
  private readonly step: ActionStepDefinition<any>;

  public static async load(step: ActionStepDefinition<any>,
                           contextManager: ContextManager,
                           stepRunner: StepRunner): Promise<Action> {
    const action = new Action(step, contextManager, stepRunner);
    await action.postConstruct();
    return action;
  }

  private constructor(step: ActionStepDefinition<any>,
                      private readonly contextManager: ContextManager,
                      private readonly stepRunner: StepRunner) {
    this.uses = step.uses;
    this.step = step;
  }

  private async postConstruct() {
    const actionDir = await this.prepareActionDirectory(this.uses, this.contextManager);

    console.debug(`Loading ${this.uses}`);
    const actionDefinition: BaseActionDefinition = this.loadActionDefinition(actionDir);
    this.action = await this.loadAction(actionDir, actionDefinition);
  }

  private async prepareActionDirectory(actionName: string, context: ContextManager) {
    const {
      remote,
      project,
      revision,
      local
    } = this.extractName(actionName);

    if (remote) {
      console.debug('Loading remote action: ' + actionName);
      if (project === undefined || revision === undefined) throw new Error(`Remote name expected for action name ${actionName}`);

      console.debug(project, revision, context.contextSnapshot.internal.actionsDirectory, project);
      await checkout({
        project,
        revision,
        cwd: context.contextSnapshot.internal.actionsDirectory,
        directory: project,
        silent: true
      }, context.contextSnapshot);
      return `${context.contextSnapshot.internal.actionsDirectory}/${project}`;
    } else {
      if (local === undefined) throw new Error(`Local path expected for action name ${actionName}`);
      return `${context.contextSnapshot.internal.workspace}/${local}`;
    }
  }

  private extractName(actionName: string): { remote?: string, project?: string, revision?: string, local?: string } {
    return /^(?<remote>(?<project>.+)@(?<revision>.+))$|^(?<local>.+)$/i.exec(actionName)?.groups as any || {};
  }

  private loadActionDefinition(actionsDirectory: string): any {
    const actionPattern = /^action\.ya?ml$/;
    const match = (allFilesPaths: string) => allFilesPaths.match(actionPattern) !== null;
    const actionFilePaths = fs.readdirSync(actionsDirectory).filter(match);
    if (actionFilePaths.length !== 1) throw new Error(`Unique action not found in the directory. Dir: ${actionsDirectory}. Found: ${actionFilePaths}`);
    return loadYamlFile<BaseActionDefinition>(`${actionsDirectory}/${actionFilePaths[0]}`);
  }

  private async loadAction(actionDir: string, actionDefinition: BaseActionDefinition): Promise<LocalActionExpression> {
    switch (actionDefinition.runs.using?.toLowerCase()) {
      case 'docker':
        throw new Error('Action type unhandled');
      case 'composite':
        const compositeAction = actionDefinition as CompositeActionDefinition;
        return this.manageCompositeAction(compositeAction);
      case 'node':
      default:
        const nodeAction = actionDefinition as NodeActionDefinition;
        const mainScript = `${actionDir}/${nodeAction.runs.main}`;
        return this.scriptRunner(mainScript);
    }
  }

  private scriptRunner(scriptPath: string): LocalActionExpression {
    return async (context, callbacks) => new Promise<ActionResult>((resolveSuccess, resolveFailure) => {
      const script = childProcess.fork(scriptPath, [JSON.stringify([this.step, context])], { cwd: process.cwd() });

      let outputs: StepOutputs | undefined;

      script.on('error', err => {
        throw new Error(`Script error: ${err}`);
      });

      script.on('message', (m: string) => {
        const message: SetOutputMessage | AddEnvMessage | AddToPathMessage = JSON.parse(m);

        switch (message.type) {
          case 'addEnv':
            let typedMessage1 = message as AddEnvMessage;
            callbacks.addEnv(typedMessage1.content.env, typedMessage1.content.value);
            return;
          case 'addToPath':
            let typedMessage2 = message as AddToPathMessage;
            callbacks.addToPath(typedMessage2.content);
            return;
          case 'setOutput':
            outputs = outputs ?? {};
            let typedMessage3 = message as SetOutputMessage;
            outputs[typedMessage3.content.key] = typeof typedMessage3.content.value === 'string' || typeof typedMessage3.content.value === 'number' ? typedMessage3.content.value : JSON.stringify(typedMessage3.content.value);
            return;
          default:
            throw new Error(`Unsupported message: ${JSON.stringify(message)}`);
        }
      });

      script.on('exit', (code: string) => {
        console.debug(`child process exited with code ${code}`);
        if (+code === 0) {
          resolveSuccess({ outcome: 'success', outputs });
        } else {
          resolveSuccess({ outcome: 'failure', outputs });
        }
      });
    });
  }

  async run(): Promise<ActionResult> {
    return this.action?.(this.contextManager.contextSnapshot, {
      addEnv: this.contextManager.addEnv,
      addToPath: this.contextManager.addToPath
    }) ?? throwThis('Action needs to be defined!');
  }

  private manageCompositeAction = (compositeAction: CompositeActionDefinition): LocalActionExpression =>
    async (_, __): Promise<ActionResult> => (async () => {
        console.debug(
          '[Creating composite action]',
          JSON.stringify(this.step, undefined, 2),
          JSON.stringify(_, undefined, 2),
          JSON.stringify(__, undefined, 2),
          JSON.stringify(compositeAction, undefined, 2)
        );
        return this.mapToStepResult(await this.stepRunner.forCompositeStep(this.step, compositeAction).run());
      }
    )();

  private mapToStepResult(jobResult: StepRunnerResult): ActionResult {
    return {
      outcome: jobResult.result,
      ...(jobResult.outputs ? { outputs: jobResult.outputs } : {})
    };
  }
}
