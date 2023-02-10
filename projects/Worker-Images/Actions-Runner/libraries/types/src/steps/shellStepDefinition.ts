import {StepDefinition} from "./stepDefinition";

export interface ShellStepDefinition extends StepDefinition {
    /**
     * The command you want to run. This can be inline or a script in your action repository:
     *
     * ${{ internal.action_path }} || $GITHUB_ACTION_PATH
     */
    run: string
    shell?: "bash"
    workingDirectory?: string
}