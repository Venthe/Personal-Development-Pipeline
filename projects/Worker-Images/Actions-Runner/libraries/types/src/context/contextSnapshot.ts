import {CurrentStatus, FinalStatus, InputOutput} from '../commonTypes';
import {GerritEventSnapshot} from "./gerritEventSnapshot";

/**
 * This context changes for each job in a workflow.ts run. You can access this context from any step in a job. This object
 * contains all the properties listed below.
 **/
export interface JobSnapshot {
    /**
     * Information about the job's container. For more information about containers, see "Workflow syntax for <SYSTEM>
     * Actions."
     */
    container: {
        /**
         * The ID of the container.
         */
        id: string;
        /**
         * The ID of the container network. The runner creates the network used by all containers in a job.
         */
        network: string;
    }
    /**
     * The service containers created for a job. For more information about service containers, see "Workflow syntax
     * for <SYSTEM> Actions."
     */
    services: {
        [serviceId: string]: {
            /**
             * The ID of the service container.
             */
            id: string;
            /**
             * The ID of the service container network. The runner creates the network used by all containers in a job.
             */
            network: string;
            /**
             * The exposed ports of the service container.
             */
            ports: object;
        }
    }
    /**
     * The current status of the job.
     */
    status: CurrentStatus;
}

export type JobOutput = {
    /**
     * The value of a specific output for a job in a reusable workflow.ts.
     */
    [outputName: string]: InputOutput
}

export type JobOutputs = {
    [jobId: string]: JobOutput
};

export type JobResultString = FinalStatus;
export type JobResult = {
    /**
     * The result of a job in the reusable workflow.ts.
     */
    result: JobResultString
    /**
     * The set of outputs of a job in a reusable workflow.ts.
     */
    outputs?: JobOutputs
};
/**
 * This is only available in reusable workflows, and can only be used to set outputs for a reusable workflow.ts. This
 * object contains all the properties listed below.
 */
export type JobResultSnapshot = {
    [jobId: string]: JobResult
}

export type StepOutputs = {
    /**
     * The value of a specific output.
     */
    [outputId: string]: InputOutput
};

export type StepResult = {
    /**
     * This context changes for each step in a job. You can access this context from any step in a job. This object contains all the properties listed below.
     * The set of outputs defined for the step. For more information, see "Metadata syntax for <SYSTEM> Actions."
     */
    outputs?: StepOutputs
    /**
     * The result of a completed step before continue-on-error is applied. Possible values are success, failure,
     * cancelled, or skipped. When a continue-on-error step fails, the outcome is failure, but the final conclusion
     * is success.
     */
    outcome: FinalStatus
    /**
     * The result of a completed step after continue-on-error is applied. Possible values are success, failure,
     * cancelled, or skipped. When a continue-on-error step fails, the outcome is failure, but the final conclusion
     * is success.
     */
    conclusion: FinalStatus
};

/**
 * This context changes for each step in a job. You can access this context from any step in a job. This object contains all the properties listed below.
 */
export interface StepsResultSnapshot {
    [stepId: string]: StepResult
}

/**
 * This context changes for each job in a workflow.ts run. This object contains all the properties listed below.
 */
export interface RunnerSnapshot {
    /**
     * The name of the runner executing the job.
     */
    name: string
    /**
     * The operating system of the runner executing the job.
     */
    os: "Linux" | "Windows" | "manOS"
    /**
     * The architecture of the runner executing the job.
     */
    arch: "X86" | "X64" | "ARM" | "ARM64"
    /**
     * The path to a temporary directory on the runner. This directory is emptied at the beginning and end of each job.
     * Note that files will not be removed if the runner's user account does not have permission to delete them.
     */
    temp: string
    /**
     * This context changes for each job in a workflow.ts run. This object contains all the properties listed below.
     * This is set only if debug logging is enabled, and always has the value of 1. It can be useful as an indicator
     * to enable additional debugging or verbose logging in your own job steps.
     */
    toolCache: string
    /**
     * This is set only if debug logging is enabled, and always has the value of 1. It can be useful as an indicator to
     * enable additional debugging or verbose logging in your own job steps.
     */
    debug?: "1"
}

/**
 * This context is the same for each job in a workflow.ts run. You can access this context from any step in a job. This
 * object contains all the properties listed below.
 */
export interface SecretsSnapshot {
    /**
     * The value of a specific secret.
     */
    [secretName: string]: string

    /**
     * Automatically created token for each workflow.ts run. For more information, see "Automatic token authentication."
     * @Deprecated
     */
    SYSTEM_TOKEN: string
    DOCKER_USERNAME: string
    DOCKER_PASSWORD: string
    NEXUS_USERNAME: string
    NEXUS_PASSWORD: string
}

/**
 * This context changes for each job in a workflow.ts run. You can access this context from any job or step in a workflow.ts.
 * This object contains all the properties listed below.
 */
export interface StrategySnapshot {
    /**
     * When true, all in-progress jobs are canceled if any job in a matrix fails. For more information, see "Workflow
     * syntax for <SYSTEM> Actions."
     */
    failFast: boolean
    /**
     * The index of the current job in the matrix. Note: This number is a zero-based number. The first job's index in
     * the matrix is 0.
     */
    jobIndex: number
    /**
     * The total number of jobs in the matrix. Note: This number is not a zero-based number. For example, for a matrix
     * with four jobs, the value of job-total is 4.
     */
    jobTotal: number
    /**
     * The maximum number of jobs that can run simulta
     *         tags?: stringneously when using a matrix job strategy. For more information,
     * see "Workflow syntax for <SYSTEM> Actions."
     */
    maxParallel: number
}

/**
 * This context is only available for jobs in a matrix, and changes for each job in a workflow.ts run. You can access this
 * context from any job or step in a workflow.ts. This object contains the properties listed below.
 */
export interface MatrixSnapshot {
    /**
     * The value of a matrix property.
     */
    [propertyName: string]: string
}

/**
 * This context is only populated for workflow.ts runs that have dependent jobs, and changes for each job in a workflow.ts run.
 * You can access this context from any job or step in a workflow.ts. This object contains all the properties listed below.
 */
export interface NeedsSnapshot {
    /**
     * A single job that the current job depends on.
     */
    [jobId: string]: {
        /**
         * The set of outputs of a job that the current job depends on.
         */
        outputs: {
            [jobId: string]: {
                /**
                 * The value of a specific output for a job that the current job depends on.
                 */
                [name: string]: string
            }
        }
        /**
         * The result of a job that the current job depends on. Possible values are success, failure, cancelled, or skipped.
         */
        result: FinalStatus
    }
}

/**
 * This context is only available in a reusable workflow.ts or in a workflow.ts triggered by the workflow_dispatch event.
 * You can access this context from any job or step in a workflow.ts. This object contains the properties listed below.
 */
export interface InputsSnapshot {
    /**
     * Each input value passed from an external workflow.ts.
     */
    [name: string]: InputOutput
}

/**
 * This context changes for each step in a job. You can access this context from any step in a job. This object
 * contains the properties listed below.
 */
type EnvironmentVariablesSnapshot = {
    [key: string]: string | undefined
};

export type InternalSnapshot<T extends object = GerritEventSnapshot> = {

    /**
     * The full event webhook payload. You can access individual properties of the event using this context. This
     * object is identical to the webhook payload of the event that triggered the workflow.ts run, and is different for
     * each event. The webhooks for each <SYSTEM> Actions event is linked in "Events that trigger workflows." For example,
     * for a workflow.ts run triggered by the push event, this object contains the contents of the push webhook payload.
     **/
    event: T

    /**
     * The default working directory on the runner for steps, and the default location of your repository when using
     * the checkout action.
     **/
    workspace: string
    binariesDirectory: string
    /**
     * Directory to where actions are downloaded to
     */
    actionsDirectory: string
    /**
     * Directory with pipeline of the initializing repository
     */
    pipelinesDirectory: string

    /**
     * The URL of the <SYSTEM> REST API.
     **/
    gerritUrl: string
    nexusUrl: string
    dockerUrl: string
    jenkinsUrl: string
    dockerHost?: string

    /**
     * The name of the action currently running, or the id of a step. <SYSTEM> removes special characters, and uses the
     * name __run when the current step runs a script without an id. If you use the same action more than once in the
     * same job, the name will include a suffix with the sequence number with underscore before it. For example, the
     * first script you run will have the name __run, and the second script will be named __run_2. Similarly, the second
     * invocation of actions/checkout will be actionscheckout2.
     **/
    action: string

    /**
     * The path where an action is located. This property is only supported in composite actions.
     * You can use this path to access files located in the same repository as the action.
     **/
    actionPath: string
    /**
     * For a step executing an action, this is the ref of the action being executed. For example, v2.
     **/
    actionRevision: string
    /**
     * For a step executing an action, this is the owner and repository name of the action. For example,
     * actions/checkout.
     **/
    actionRepository: string
    /**
     * For a composite action, the current result of the composite action.
     **/
    actionStatus: string
    /**
     * The username of the user that triggered the initial workflow.ts run. If the workflow.ts run is a re-run, this value
     * may differ from <system>.triggering_actor. Any workflow.ts re-runs will use the privileges of <system>.actor, even if
     * the actor initiating the re-run (<system>.triggering_actor) has different privileges.
     **/
    actor: string
    /**
     * The base_ref or target branch of the pull request in a workflow.ts run. This property is only available when the
     * event that triggers a workflow.ts run is either pull_request or pull_request_target.
     **/
    baseRevisions: string
    /**
     * Path on the runner to the file that sets environment variables from workflow.ts commands. This file is unique to
     * the current step and is a different file for each step in a job. For more information, see "Workflow commands
     * for <SYSTEM> Actions."
     **/
    env: string
    /**
     * The name of the event that triggered the workflow.ts run.
     **/
    eventName: string
    /**
     * The path to the file on the runner that contains the full event webhook payload.
     **/
    eventPath: string
    /**
     * The head_ref or source branch of the pull request in a workflow.ts run. This property is only available when
     * the event that triggers a workflow.ts run is either pull_request or pull_request_target.
     **/
    headRef: string
    /**
     * The job_id of the current job.
     **/
    job: string
    /**
     * Note: This context property is set by the Actions runner, and is only available within the execution steps of a
     * job. Otherwise, the value of this property will be null.
     **/
    ref: string
    /**
     * The fully-formed ref of the branch or tag that triggered the workflow.ts run. For workflows triggered by push, this
     * is the branch or tag ref that was pushed. For workflows triggered by pull_request, this is the pull request merge
     * branch. For workflows triggered by release, this is the release tag created. For other triggers, this is the
     * branch or tag ref that triggered the workflow.ts run. This is only set if a branch or tag is available for the
     * event type. The ref given is fully-formed, meaning that for branches the format is refs/heads/<branch_name>,
     * for pull requests it is refs/pull/<pr_number>/merge, and for tags it is refs/tags/<tag_name>. For example,
     * refs/heads/feature-branch-1.
     **/
    refName: string
    /**
     * The short ref name of the branch or tag that triggered the workflow.ts run. This value matches the branch or tag
     * name shown on <SYSTEM>. For example, feature-branch-1.
     **/
    refProtected: boolean
    /**
     * true if branch protections are configured for the ref that triggered the workflow.ts run.
     **/
    refType: string
    /**
     * The type of ref that triggered the workflow.ts run. Valid values are branch or tag.
     **/
    path: string
    /**
     * Path on the runner to the file that sets system PATH variables from workflow.ts commands. This file is unique to
     * the current step and is a different file for each step in a job. For more information, see "Workflow commands
     * for <SYSTEM> Actions."
     **/
    repository: string
    /**
     * The owner and repository name. For example, Codertocat/Hello-World.
     **/
    repositoryOwner: string
    /**
     * The repository owner's name. For example, Codertocat.
     **/
    repositoryUrl: string
    /**
     * The Git URL to the repository. For example, git://<system>.com/codertocat/hello-world.git.
     **/
    retentionDays: string
    /**
     * The number of days that workflow.ts run logs and artifacts are kept.
     **/
    /**
     * A unique number for each run of a particular workflow.ts in a repository. This number begins at 1 for the workflow.ts's
     * first run, and increments with each new run. This number does not change if you re-run the workflow.ts run.
     **/
    runId: string
    /**
     * A unique number for each attempt of a particular workflow.ts run in a repository. This number begins at 1 for the
     * workflow.ts run's first attempt, and increments with each re-run.
     **/
    runNumber: string
    /**
     * The source of a secret used in a workflow.ts. Possible values are None, Actions, Dependabot, or Codespaces.
     **/
    runAttempt: string
    /**
     * The URL of the <SYSTEM> server. For example: https://<system>.com.
     **/
    secretSource: string
    /**
     * The commit SHA that triggered the workflow.ts. The value of this commit SHA depends on the event that triggered the
     * workflow.ts. For more information, see "Events that trigger workflows."
     * For example, ffac537e6cbbf934b08745a378932722df287a53.
     **/
    serverUrl: string
    /**
     * The username of the user that initiated the workflow.ts run. If the workflow.ts run is a re-run, this value may differ
     * from <system>.actor. Any workflow.ts re-runs will use the privileges of <system>.actor, even if the actor initiating the
     * re-run (<system>.triggering_actor) has different privileges.
     **/
    triggeringActor: string
    /**
     * The name of the workflow.ts. If the workflow.ts file doesn't specify a name, the value of this property is the full
     * path of the workflow.ts file in the repository.
     **/
    workflow: string
    /**
     * The URL of the <SYSTEM> GraphQL API.
     * @Deprecated
     **/
    graphqlUrl: string
    /**
     * Note: This context property is set by the Actions runner, and is only available within the execution steps of a
     * job. Otherwise, the value of this property will be null.
     * @Deprecated
     **/
    token: string
    /**
     * A token to authenticate on behalf of the <SYSTEM> App installed on your repository. This is functionally equivalent
     * to the SYSTEM_TOKEN secret. For more information, see "Automatic token authentication."
     * @Deprecated
     **/
    sha: string
};

/**
 * The top-level context available during any job or step in a workflow.ts. This object contains all the properties
 * listed below.
 **/
export interface ContextSnapshot<T extends object = GerritEventSnapshot> {
    internal: InternalSnapshot<T>
    env: EnvironmentVariablesSnapshot
    job?: JobSnapshot
    jobs?: JobResultSnapshot
    steps?: StepsResultSnapshot
    runner: RunnerSnapshot
    secrets: SecretsSnapshot
    strategy: StrategySnapshot
    matrix?: MatrixSnapshot
    needs?: NeedsSnapshot
    inputs?: InputsSnapshot
}
