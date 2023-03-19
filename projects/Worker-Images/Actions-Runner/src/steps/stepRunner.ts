import {
  ActionStepDefinition,
  CompositeActionDefinition,
  FinalStatus,
  JobDefinition,
  JobOutput,
  ShellStepDefinition,
  StepResult
} from '@pipeline/types';
import { ContextManager } from '../context/contextManager';
import { StepFactory } from './stepFactory';
import { error, forceRun, running, step } from '@pipeline/utilities';
import { rerenderTemplate } from '../utilities/template';
import { exceptionMapper } from '../utilities';
import { ActionResult } from './actions';
import { shouldRunExpression } from './script';

type OutputMappings = {
  // ${{ steps.step1.outputs.test }}
  [output: string]: string
};

export type StepRunnerResult = {
  result: FinalStatus
  outputs?: JobOutput
}

export class StepRunner {
  readonly managerName: string;
  private steps: (ActionStepDefinition | ShellStepDefinition)[] = [];
  private readonly nextIndex: () => number;
  private readonly contextManager: ContextManager;
  private readonly outputs?: OutputMappings;

  public forCompositeStep(step: ActionStepDefinition<any>,
                          compositeStep: CompositeActionDefinition): StepRunner {
    const outputs = Object.keys(compositeStep.outputs ?? {}).reduce((acc, key) => {
      acc[key] = (compositeStep?.outputs || {})[key].value;
      return acc;
    }, {} as OutputMappings);
    console.debug('[Creating new StepRunner]',
      JSON.stringify(step, undefined, 2),
      JSON.stringify(compositeStep, undefined, 2),
      JSON.stringify(outputs, undefined, 2)
    );

    return new StepRunner({
      stepDefinitions: compositeStep.runs.steps,
      contextManager: this.contextManager.forComposite(step),
      managerName: `${this.managerName}|${compositeStep.name}`,
      nextIndex: this.nextIndex,
      outputs
    });
  }

  public static forJob(stepDefinitions: JobDefinition,
                       contextManager: ContextManager): StepRunner {
    const iter = { value: 1 };

    return new StepRunner({
      stepDefinitions: stepDefinitions.steps,
      contextManager: contextManager,
      managerName: stepDefinitions.name ?? contextManager.contextSnapshot.internal.job,
      nextIndex: () => iter.value++,
      outputs: stepDefinitions.outputs
    });
  }

  constructor({ stepDefinitions, contextManager, nextIndex, ...rest }: {
    stepDefinitions: (ActionStepDefinition | ShellStepDefinition)[],
    contextManager: ContextManager,
    managerName: string,
    nextIndex: () => number,
    outputs?: OutputMappings
  }) {
    this.managerName = rest.managerName;
    this.steps = stepDefinitions;
    this.contextManager = contextManager;
    this.nextIndex = nextIndex;
    this.outputs = rest.outputs;
  }

  public async run(): Promise<StepRunnerResult> {
    try {
      while (this.steps.length > 0) {
        const mappedStep = StepFactory.from(this.getCurrentStep(), this.nextIndex());

        console.log(step(`[${mappedStep.id}][${this.managerName}]: ${mappedStep.name}`) + ` - ${this.stateSuffix(mappedStep)}`);

        if (!this.isAnyStepConclusionFailure() || this.shouldRunFromScript(mappedStep)) {
          const result: ActionResult = await mappedStep.run(this, this.contextManager);
          // FIXME: Name added for convenience
          const res: StepResult  = { ...result, conclusion: result.outcome, name: mappedStep.name } as StepResult;
          this.contextManager.setResult(mappedStep.id, res);
        } else {
          // FIXME: Name added for convenience
          this.contextManager.setResult(mappedStep.id, { outcome: 'skipped', conclusion: 'skipped', name: mappedStep.name } as StepResult);
        }

        console.debug(
          '[Step finished]', mappedStep.name,
          JSON.stringify(this.outputs, undefined, 2),
          JSON.stringify(this.contextManager.contextSnapshot.steps, undefined, 2)
        );
      }

      const outputs = rerenderTemplate<JobOutput>(this.outputs ?? {}, this.contextManager.contextSnapshot);
      console.debug('[Runner finished]', this.managerName,
        JSON.stringify(this.outputs, undefined, 2),
        JSON.stringify(this.contextManager.contextSnapshot.steps, undefined, 2)
      );
      return this.isAnyStepConclusionFailure() ? { result: 'failure' } : {
        result: 'success',
        outputs: outputs
      };
    } catch (e: any) {
      console.error(error(exceptionMapper(e)));
      return {
        result: 'failure'
      };
    }
  }

  private stateSuffix(mappedStep): string {
    if (this.isAnyStepConclusionFailure() && !this.shouldRunFromScript(mappedStep)) {
      return forceRun('Skipped'.toUpperCase());

    }
    if (this.isAnyStepConclusionFailure()) {
      return error('Force run'.toUpperCase());
    }

    return running('Running'.toUpperCase());
  }

  private shouldRunFromScript(mappedStep) {
    if (!mappedStep.if) {
      return false;
    }

    return shouldRunExpression(this.contextManager.contextSnapshot, mappedStep.if);
  };

  private isAnyStepConclusionFailure() {
    let steps = this.contextManager.contextSnapshot?.steps || {};
    return !!Object.keys(steps).map(key => steps[key]).map(a => a.outcome).filter(a => a.toLocaleLowerCase().includes('failure'))[0];
  }

  private getCurrentStep(): ActionStepDefinition | ShellStepDefinition {
    const currentStep: ActionStepDefinition | ShellStepDefinition | undefined = this.steps.shift();
    if (currentStep === undefined) throw new Error('Step should never be undefined');
    return currentStep;
  }
}
