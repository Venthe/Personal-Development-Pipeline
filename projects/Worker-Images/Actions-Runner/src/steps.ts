// import {ActionStep, Context, ShellStep, Step} from "./types";
// import {shell} from "./shell";
//
// export const withContext = (act, ctx: Context) => async (step) => await act(step, ctx)
//
// function isShellStep(step: Step): step is ActionStep {
//     return (step as ActionStep).uses === undefined;
// }
//
// export const handleStep = async (step2: Step, context) => {
//
//     if (isShellStep(step2)) {
//         return withContext(shell, context)
//     }
//
//     const step = step2 as ActionStep
//
//     const uses: string = step.uses
//
//     const rawAction = await downloadAction(uses)
//     const parsedActon = bindContextToAction(await parseAction(rawAction));
//     return executeAction(parsedActon)
// }
//
// type RawAction = Record<string, never>;
// type ParsedAction = Record<string, never>;
//
// const downloadAction = (uses: string): RawAction => {
//     throw new Error("Not implemented")
// }
//
// const parseAction = async (action: RawAction): Promise<ParsedAction> => {
//     throw new Error("Not implemented")
// }
// const bindContextToAction = (action: ParsedAction): ParsedAction => {
//     throw new Error("Not implemented")
// }
// const executeAction = (action: ParsedAction): Promise<void> => {
//     throw new Error("Not implemented")
// }
//
// export const shouldRun = (stepIf, context, anyPreviousStepFailed) => {
//     if (stepIf === undefined) {
//         console.debug("stepIf is undefined, and the anyPreviousStepFailed", anyPreviousStepFailed)
//         return !anyPreviousStepFailed
//     }
//
//     if (stepIf === "always()") {
//         console.debug("stepIf is marked as always()")
//         return true;
//     }
//
//     console.debug("calculating, anyPreviousStepFailed", anyPreviousStepFailed)
//     return (new Function(`{env, steps, isFailed, ...rest} = ${JSON.stringify(context)}`, `return ${stepIf}`))() && !anyPreviousStepFailed
// }
//
// export const getStepName = (step) => step.name ?? step.uses ?? "Shell"

import {shell} from "libraries/process";
import {Context} from "./configuration";
import {StepDefinition, ActionStep as ActionStepType, ShellStep as ShellStepType} from "./types/steps";
import {Actions} from "./actions";
import * as process from "process";

export class StepManager {
    constructor(private readonly context: Context) {
    }

    async runSteps() {
        const {steps} = this.context.getJob()
        //
        // const anyPreviousStepFailed = () => steps.map(a => a.isFailed).some(a => a === true)
        //
        for (const el of steps.map(stepDefinition => Step.create(stepDefinition))) {
            try {
                await el.run(this.context);
            } catch (e) {
                console.error(e)
                process.exit(1);
                return
            }
        }

        // console.log(step)
        //     const finalName = getStepName(step);
        //     if (!shouldRun(step.if, context, anyPreviousStepFailed())) {
        //         console.log(`Skipping step ${finalName}...`)
        //         console.log()
        //         continue
        //     }
        //
        //     await context.updateEnv(context)
        //
        //     console.log(`[${index}]`, clc.white.bold(finalName))
        //     try {
        //         await handleStep(step, context)
        //     } catch (exception: any) {
        //         console.error(clc.red(exception.reason))
        //         step.isFailed = true
        //         context.reason = [...(context.reason || []), exception]
        //     }
        // }
        //
        // if (anyPreviousStepFailed()) {
        //     throw context.reason
    }
}

abstract class Step<T extends StepDefinition> {
    readonly step: T;

    protected constructor(private readonly stepDefinition: T) {
        this.step = stepDefinition
    }

    public async run(context: Context) {
        console.log(`[${this.getType()}]: ${this.step.name ?? this.getName()}`)
        await this._run(context)
    }

    protected abstract _run(context: Context)

    static create(stepDefinition: StepDefinition) {
        if ((stepDefinition as ActionStepType).uses) {
            return new ActionStep(stepDefinition as ActionStepType)
        } else {
            return new ShellStep(stepDefinition as ShellStepType)
        }
    }

    abstract getType(): string

    abstract getName(): string
}

class ActionStep extends Step<ActionStepType> {
    constructor(stepDefinition: ActionStepType) {
        super(stepDefinition);
    }

    getType(): string {
        return "Action";
    }

    getName(): string {
        return this.step.uses;
    }

    async _run(context: Context) {
        await new Actions(this.step).run(context);
    }
}

class ShellStep extends Step<ShellStepType> {
    constructor(stepDefinition: ShellStepType) {
        super(stepDefinition);
    }

    getType(): string {
        return "Shell";
    }

    getName(): string {
        return "Shell";
    }

    async _run(context: Context) {
        let workingDirectoryOption = this.step.workingDirectory ? {cwd: this.step.workingDirectory} : {};
        let shellOption = this.step.shell ? {cwd: this.step.shell} : {};
        await shell(this.step.run, {...shellOption, ...workingDirectoryOption})
    }
}
