import { ActionStepDefinition } from '@pipeline/types';
import { StepRunner } from './stepRunner';
import { ContextManager } from '../context/contextManager';
import { getId as gid, Step } from './step';
import { throwThis } from '@pipeline/utilities';
import { Action, ActionResult } from './actions';

export class ActionStep<T extends object = {}> implements Step<ActionStepDefinition<T>> {
  constructor(private readonly step: ActionStepDefinition<T>, private readonly index: number) {
  }

  get name(): string {
    return this.step.name ?? this.step.id ?? this.step.uses;
  }

  async run(parentStepRunner: StepRunner,
            contextManager: ContextManager): Promise<ActionResult> {
    try {
      const action = await Action.load(this.step, contextManager, parentStepRunner);
      return action.run();
    } catch (e) {
      return { outcome: "failure" }
    }
  }

  get uses(): string {
    return this.step.uses ?? throwThis('No action found for step definition. Step definition \'uses\' field should not be null');
  }

  get id(): string {
    return gid(this.step, this.index);
  }

  get with(): T | undefined {
    return this.step.with;
  }

  get if(): string | undefined {
    return this.step.if
  }
}
