import {Context, ContextEnvironmentVariables} from "./configuration";
import {configureGit} from "./git";
import * as process from "process";
import {keyValue} from "./libraries/utilities";
import {StepManager} from "./steps";
import {shell} from "./libraries/process";

export const main = async () => {
    await configureGit()
    const context = await Context.create(process.env as ContextEnvironmentVariables);

    console.log("Running actions manager", keyValue("buildID", context.buildId))

    const stepManager = new StepManager(context);
    stepManager.runSteps()
};
