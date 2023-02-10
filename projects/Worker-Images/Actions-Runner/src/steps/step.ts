import { StepDefinition } from '@pipeline/types';
import { StepRunner } from './stepRunner';
import { ContextManager } from '../context/contextManager';
import { ActionResult } from './actions';

export const getId = (step: StepDefinition, idx: number) => step.id ?? `step_${idx}`;

export interface Step<T extends StepDefinition> {
  id: string;
  run: (parentStepRunner: StepRunner,
        contextManager: ContextManager) => Promise<ActionResult>;
  name: string;
  if?: string;
}

