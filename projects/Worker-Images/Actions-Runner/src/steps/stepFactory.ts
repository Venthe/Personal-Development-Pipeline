import { ActionStepDefinition, ShellStepDefinition } from '@pipeline/types';
import { ActionStep } from './actionStep';
import { ShellStep } from './shellStep';

export class StepFactory {

  public static from(stepDefinition: ActionStepDefinition | ShellStepDefinition, index: number): ActionStep | ShellStep {
    if ((stepDefinition as ActionStepDefinition).uses) {
      return new ActionStep(stepDefinition as ActionStepDefinition, index);
    } else {
      return new ShellStep(stepDefinition as ShellStepDefinition, index);
    }
  }
}
