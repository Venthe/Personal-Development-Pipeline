import {Dictionary} from "./common";

export interface ShellStep extends Step {
    /**
     * The command you want to run. This can be inline or a script in your action repository:
     *
     * ${{ github.action_path }} || $GITHUB_ACTION_PATH
     */
    run?: string
    shell?: "bash"
    workingDirectory?: string
}

export interface ActionStep<T extends Dictionary<string | undefined> = {}> extends Step {
    uses: string
    with?: T
}

export interface Step {
    if?: string
    name?: string
    id?: string
}

export type StepDefinition = ActionStep | ShellStep;
