import { shellMany } from '@pipeline/process';
import { step } from '@pipeline/core';
import { ActionStepDefinition } from '@pipeline/types';

(async () => await shellMany(
  ((step as ActionStepDefinition<{ libraries: string[] }>).with?.libraries ?? [])
    .map(a => `pip3 install ${a}`)
))();