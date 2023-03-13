import {shellMany} from '@pipeline/process';
import {step} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';

(async () => {
    const _with = (step as ActionStepDefinition<{ libraries?: string[], requirements?: string[] }>).with ?? {}
    const command = (args: string[]) => `pip3 install ${args.join(" ")}`
    const commandWrapper = (args: string[]) => args.length > 0 ? [command(args)] : []

    const commandsToBeExecuted = [
        ...commandWrapper(_with?.libraries ?? []),
        ...commandWrapper((_with?.requirements ?? [])
            .map(requirementFile => `-r ${requirementFile}`))]
    await shellMany(commandsToBeExecuted);
})();
