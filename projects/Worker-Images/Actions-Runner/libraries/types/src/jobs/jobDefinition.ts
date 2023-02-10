import {ConcurrencyDefinition, Defaults, Permissions} from "../commonDefinitions";
import { Expression, InputOutput } from '../commonTypes';
import {ActionStepDefinition, DockerStepDefinition, ShellStepDefinition} from "../steps";
import {Container} from "./container";
import {JobId} from "./jobsDefinition";

type RunnerLabel = string;
type OperatingSystem = string;
type RunnerGroup = string;
type Strategy = {
    failFast: boolean
    matrix: {
        [key: string]: { [key: string]: number | boolean | string } | (string | number)[]
    }
};
export type JobDefinition = {
    /**
     * Use jobs.<job_id>.name to set a name for the job, which is displayed in the GitHub UI.
     */
    name: string
    /**
     * For a specific job, you can use jobs.<job_id>.permissions to modify the default permissions granted to
     * the GITHUB_TOKEN, adding or removing access as required, so that you only allow the minimum required access.
     * For more information, see "Authentication in a workflow."
     *
     * By specifying the permission within a job definition, you can configure a different set of permissions
     * for the GITHUB_TOKEN for each job, if required. Alternatively, you can specify the permissions for all
     * jobs in the workflow. For information on defining permissions at the workflow level, see permissions.
     */
    permissions?: Permissions
    /**
     * Use jobs.<job_id>.needs to identify any jobs that must complete successfully before this job will run.
     * It can be a string or array of strings. If a job fails, all jobs that need it are skipped unless the
     * jobs use a conditional expression that causes the job to continue. If a run contains a series of jobs
     * that need each other, a failure applies to all jobs in the dependency chain from the point of failure
     * onwards.
     */
    needs: JobId | JobId[]
    /**
     * You can use the jobs.<job_id>.if conditional to prevent a job from running unless a condition is met.
     * You can use any supported context and expression to create a conditional. For more information on which
     *contexts are supported in this key, see "Context availability."
     *
     * When you use expressions in an if conditional, you may omit the expression syntax (${{ }}) because
     * GitHub automatically evaluates the if conditional as an expression. For more information,
     * see "Expressions."
     */
    if?: Expression
    /**
     * Use jobs.<job_id>.runs-on to define the type of machine to run the job on.
     *
     *     The destination machine can be either a GitHub-hosted runner, larger runner, or a self-hosted runner.
     *     You can target runners based on the labels assigned to them, or their group membership, or a
     *     combination of these.
     *     You can provide runs-on as a single string or as an array of strings.
     *     If you specify an array of strings, your workflow will execute on any runner that matches all of the
     *     specified runs-on values.
     *     If you would like to run your workflow on multiple machines, use jobs.<job_id>.strategy.
     */
    runsOn: OperatingSystem | ({ group: RunnerGroup } | RunnerLabel[])
    /**
     * Use jobs.<job_id>.environment to define the environment that the job references. All environment
     * protection rules must pass before a job referencing the environment is sent to a runner. For more
     * information, see "Using environments for deployment."
     *
     * You can provide the environment as only the environment name, or as an environment object with the name
     * and url. The URL maps to environment_url in the deployments API. For more information about the
     * deployments API, see "Deployments."
     */
    environment?: string | { name: string, url: string }
    /**
     * You can use jobs.<job_id>.concurrency to ensure that only a single job or workflow using the same
     * concurrency group will run at a time. A concurrency group can be any string or expression.
     * The expression can use any context except for the secrets context. For more information about
     * expressions, see "Expressions."
     *
     * You can also specify concurrency at the workflow level. For more information, see concurrency.
     *
     * When a concurrent job or workflow is queued, if another job or workflow using the same concurrency
     * group in the repository is in progress, the queued job or workflow will be pending. Any previously
     * pending job or workflow in the concurrency group will be canceled. To also cancel any currently running
     * job or workflow in the same concurrency group, specify cancel-in-progress: true.
     */
    concurrency?: ConcurrencyDefinition
    /**
     * You can use jobs.<job_id>.outputs to create a map of outputs for a job. Job outputs are available to all
     * downstream jobs that depend on this job. For more information on defining job dependencies,
     * see jobs.<job_id>.needs.
     *
     * Outputs are Unicode strings, and can be a maximum of 1 MB. The total of all outputs in a workflow run can
     * be a maximum of 50 MB.
     *
     * Job outputs containing expressions are evaluated on the runner at the end of each job. Outputs containing
     * secrets are redacted on the runner and not sent to GitHub Actions.
     *
     * To use job outputs in a dependent job, you can use the needs context. For more information, see "Contexts."
     */
    outputs?: {
        // ${{ steps.step1.outputs.test }}
        [output: string]: string
    }
    /**
     * A map of variables that are available to all steps in the job. You can set variables for the entire
     * workflow or an individual step. For more information, see env and jobs.<job_id>.steps[*].env.
     *
     * When more than one environment variable is defined with the same name, GitHub uses the most specific
     * variable. For example, an environment variable defined in a step will override job and workflow
     * environment variables with the same name, while the step executes. An environment variable defined for
     * a job will override a workflow variable with the same name, while the job executes.
     */
    env?: {
        [key: string]: string
    }
    /**
     * Use jobs.<job_id>.defaults to create a map of default settings that will apply to all steps in the job.
     * You can also set default settings for the entire workflow. For more information, see defaults.
     *
     * When more than one default setting is defined with the same name, GitHub uses the most specific default
     * setting. For example, a default setting defined in a job will override a default setting that has the
     * same name defined in a workflow.
     */
    defaults?: Defaults
    /**
     * A job contains a sequence of tasks called steps. Steps can run commands, run setup tasks, or run an
     * action in your repository, a public repository, or an action published in a Docker registry. Not all
     * steps run actions, but all actions run as a step. Each step runs in its own process in the runner
     * environment and has access to the workspace and filesystem. Because steps run in their own process,
     * changes to environment variables are not preserved between steps. GitHub provides built-in steps to set
     * up and complete a job.
     *
     * You can run an unlimited number of steps as long as you are within the workflow usage limits. For more
     * information, see "Usage limits and billing" for GitHub-hosted runners and "About self-hosted runners"
     * for self-hosted runner usage limits.
     */
    steps: (DockerStepDefinition | ShellStepDefinition | ActionStepDefinition)[]
    /**
     * Default: 360m
     */
    timeoutMinutes?: number
    strategy?: Strategy
    continueOnError?: boolean
    container?: Container
    services?: {
        [serviceId: string]: Container
    }
};