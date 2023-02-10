export type Event =
    "issue_comment"
    | "label"
    | "issues"
    | "push"
    | "pull_request"
    | "pull_request_target"
    | "schedule"
    | string


export type ChoiceType = "string" | "boolean" | "choice" | "environment"

export interface BaseInput<U extends ChoiceType, T> {
    description: string
    required?: boolean
    type: U
    default?: T
}

export type ChoiceInput = BaseInput<"choice", string> & {
    options: string[]
}

export type BooleanInput = BaseInput<"boolean", boolean>

export type StringInput = BaseInput<"string", string>

export type EnvironmentInput = BaseInput<"environment", string>

export type WorkflowInput = ChoiceInput | BooleanInput | StringInput | EnvironmentInput

/**
 * @Deprecated
 */
export interface OnWorkflowDispatch {
    inputs: { [inputId: string]: WorkflowInput }
}

/**
 * For example, a workflow with the following on value will run when a push is made to any branch in the workflow's
 * repository:
 */
export type OnSingleEvent = string;
/**
 * You can specify a single event or multiple events. For example, a workflow with the following on value will run when
 * a push is made to any branch in the repository or when someone forks the repository:
 *
 * If you specify multiple events, only one of those events needs to occur to trigger your workflow. If multiple
 * triggering events for your workflow occur at the same time, multiple workflow runs will be triggered.
 */
export type OnMultipleEvents = OnSingleEvent[]

export type LabelType = string

/**
 * Some events have activity types that give you more control over when your workflow should run.
 * Use on.<event_name>.types to define the type of event activity that will trigger a workflow run.
 *
 * For example, the issue_comment event has the created, edited, and deleted activity types. If your workflow
 * triggers on the label event, it will run whenever a label is created, edited, or deleted. If you specify the created
 * activity type for the label event, your workflow will run when a label is created but not when a label is edited or deleted.
 *
 * on:
 *   label:
 *     types:
 *       - created
 *
 * If you specify multiple activity types, only one of those event activity types needs to occur to trigger
 * your workflow. If multiple triggering event activity types for your workflow occur at the same time, multiple workflow
 * runs will be triggered. For example, the following workflow triggers when an issue is opened or labeled. If an issue
 * with two labels is opened, three workflow runs will start: one for the issue opened event and two for the two issue
 * labeled events.
 *
 * on:
 *   issues:
 *     types:
 *       - opened
 *       - labeled
 *
 * For more information about each event and their activity types, see "Events that trigger workflows."
 * @Deprecated
 */
export interface OnActivityType {
    [label: Event]: {
        types: LabelType | LabelType[]
    }
}

export interface PathFilter {
    path?: string
    ignorePath?: string
}

export interface BranchFilter {
    branches?: string
    branchesIgnore?: string
}

export interface TagFilter {
    tags?: string
    tagsIgnore?: string
}

/**
 * Some events have filters that give you more control over when your workflow should run.
 *
 * For example, the push event has a branches filter that causes your workflow to run only when a push to a branch that
 * matches the branches filter occurs, instead of when any push occurs.
 * @Deprecated
 */
export interface OnFilters {
    pull_request: BranchFilter | PathFilter
    pull_request_target: BranchFilter | PathFilter
    push: BranchFilter | PathFilter | TagFilter
    schedule: { cron: string }[]
}

/**
 * @Deprecated
 */
export interface OnWorkflowCall {
    inputs: { [inputId: string]: WorkflowInput }
    outputs: {
        [outputId: string]: {
            [key: string]: {
                description?: string,
                value: string
            }
        }
    }
    secrets: {
        [secretId: string]: {
            description: string
            required?: boolean
        }
    }
}

/**
 * @Deprecated
 */
export type OnWorkflowRun = {
    workflows: string[]
    types: string[]
} & BranchFilter

