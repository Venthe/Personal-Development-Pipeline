import {
    OnActivityType,
    OnFilters,
    OnMultipleEvents,
    OnSingleEvent,
    OnWorkflowCall,
    OnWorkflowDispatch,
    OnWorkflowRun
} from "./workflowActivationEvent";
import {ConcurrencyDefinition, Defaults, Permissions} from "../commonDefinitions";
import {JobsDefinition} from "../jobs/jobsDefinition";

/**
 * A workflow is a configurable automated process made up of one or more jobs. You must create a YAML file to define
 * your workflow configuration.
 */
export interface Workflow {
    /**
     * The name of your workflow. GitHub displays the names of your workflows on your repository's "Actions" tab.
     * If you omit name, GitHub sets it to the workflow file path relative to the root of the repository.
     */
    name: string
    /**
     * The name for workflow runs generated from the workflow. GitHub displays the workflow run name in the list of
     * workflow runs on your repository's "Actions" tab. If run-name is omitted or is only whitespace, then the run
     * name is set to event-specific information for the workflow run. For example, for a workflow triggered by a
     * push or pull_request event, it is set as the commit message.
     *
     * This value can include expressions and can reference the github and inputs contexts.
     *
     * run-name: Deploy to ${{ inputs.deploy_target }} by @${{ github.actor }}
     */
    runName?: string
    on: OnSingleEvent
        | OnMultipleEvents
        | OnActivityType
        | OnFilters
        | OnWorkflowCall
        | OnWorkflowRun
        | OnWorkflowDispatch
    /**
     * You can use permissions to modify the default permissions granted to the GITHUB_TOKEN, adding or removing access
     * as required, so that you only allow the minimum required access. For more information, see "Authentication
     * in a workflow."
     *
     * You can use permissions either as a top-level key, to apply to all jobs in the workflow, or within specific jobs.
     * When you add the permissions key within a specific job, all actions and run commands within that job that use the
     * GITHUB_TOKEN gain the access rights you specify. For more information, see jobs.<job_id>.permissions.
     * @Deprecated
     */
    permissions?: Permissions
    /** A map of variables that are available to the steps of all jobs in the workflow. You can also set variables that
     * are only available to the steps of a single job or to a single step. For more information, see jobs.<job_id>.env
     * and jobs.<job_id>.steps[*].env.
     *
     * Variables in the env map cannot be defined in terms of other variables in the map.
     *
     * When more than one environment variable is defined with the same name, GitHub uses the most specific variable.
     * For example, an environment variable defined in a step will override job and workflow environment variables with
     * the same name, while the step executes. An environment variable defined for a job will override a workflow
     * variable with the same name, while the job executes.
     */
    env?: {
        [key: string]: string | undefined
    }
    /**
     * Use defaults to create a map of default settings that will apply to all jobs in the workflow. You can also set
     * default settings that are only available to a job. For more information, see jobs.<job_id>.defaults.
     *
     * When more than one default setting is defined with the same name, GitHub uses the most specific default setting.
     * For example, a default setting defined in a job will override a default setting that has the same name defined
     * in a workflow.
     * @Deprecated
     */
    defaults?: Defaults
    /**
     * Use concurrency to ensure that only a single job or workflow using the same concurrency group will run at a time.
     * A concurrency group can be any string or expression. The expression can only use the github context. For more
     * information about expressions, see "Expressions."
     *
     * You can also specify concurrency at the job level. For more information, see jobs.<job_id>.concurrency.
     *
     * When a concurrent job or workflow is queued, if another job or workflow using the same concurrency group in the
     * repository is in progress, the queued job or workflow will be pending. Any previously pending job or workflow in
     * the concurrency group will be canceled. To also cancel any currently running job or workflow in the same
     * concurrency group, specify cancel-in-progress: true.
     * @Deprecated
     */
    concurrency?: ConcurrencyDefinition
    /**
     * A workflow run is made up of one or more jobs, which run in parallel by default. To run jobs sequentially, you
     * can define dependencies on other jobs using the jobs.<job_id>.needs keyword.
     *
     * Each job runs in a runner environment specified by runs-on.
     *
     * You can run an unlimited number of jobs as long as you are within the workflow usage limits. For more information,
     * see "Usage limits and billing" for GitHub-hosted runners and "About self-hosted runners" for self-hosted runner
     * usage limits.
     *
     * If you need to find the unique identifier of a job running in a workflow run, you can use the GitHub API.
     * For more information, see "Workflow Jobs."
     */
    jobs: JobsDefinition
}

