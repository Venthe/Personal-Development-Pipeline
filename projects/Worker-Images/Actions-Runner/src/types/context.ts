import {Dictionary} from "./common";

/**
 * The top-level context available during any job or step in a workflow.ts. This object contains all the properties
 * listed below.
 **/
export interface Github {
    /**
     * The name of the action currently running, or the id of a step. GitHub removes special characters, and uses the
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
    action_path: string
    /**
     * For a step executing an action, this is the ref of the action being executed. For example, v2.
     **/
    action_ref: string
    /**
     * For a step executing an action, this is the owner and repository name of the action. For example,
     * actions/checkout.
     **/
    action_repository: string
    /**
     * For a composite action, the current result of the composite action.
     **/
    action_status: string
    /**
     * The username of the user that triggered the initial workflow.ts run. If the workflow.ts run is a re-run, this value
     * may differ from github.triggering_actor. Any workflow.ts re-runs will use the privileges of github.actor, even if
     * the actor initiating the re-run (github.triggering_actor) has different privileges.
     **/
    actor: string
    /**
     * The URL of the GitHub REST API.
     **/
    api_url: string
    /**
     * The base_ref or target branch of the pull request in a workflow.ts run. This property is only available when the
     * event that triggers a workflow.ts run is either pull_request or pull_request_target.
     **/
    base_ref: string
    /**
     * Path on the runner to the file that sets environment variables from workflow.ts commands. This file is unique to
     * the current step and is a different file for each step in a job. For more information, see "Workflow commands
     * for GitHub Actions."
     **/
    env: string
    /**
     * The full event webhook payload. You can access individual properties of the event using this context. This
     * object is identical to the webhook payload of the event that triggered the workflow.ts run, and is different for
     * each event. The webhooks for each GitHub Actions event is linked in "Events that trigger workflows." For example,
     * for a workflow.ts run triggered by the push event, this object contains the contents of the push webhook payload.
     **/
    event: object
    /**
     * The name of the event that triggered the workflow.ts run.
     **/
    event_name: string
    /**
     * The path to the file on the runner that contains the full event webhook payload.
     **/
    event_path: string
    /**
     * The URL of the GitHub GraphQL API.
     **/
    graphql_url: string
    /**
     * The head_ref or source branch of the pull request in a workflow.ts run. This property is only available when
     * the event that triggers a workflow.ts run is either pull_request or pull_request_target.
     **/
    head_ref: string
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
    ref_name: string
    /**
     * The short ref name of the branch or tag that triggered the workflow.ts run. This value matches the branch or tag
     * name shown on GitHub. For example, feature-branch-1.
     **/
    ref_protected: boolean
    /**
     * true if branch protections are configured for the ref that triggered the workflow.ts run.
     **/
    ref_type: string
    /**
     * The type of ref that triggered the workflow.ts run. Valid values are branch or tag.
     **/
    path: string
    /**
     * Path on the runner to the file that sets system PATH variables from workflow.ts commands. This file is unique to
     * the current step and is a different file for each step in a job. For more information, see "Workflow commands
     * for GitHub Actions."
     **/
    repository: string
    /**
     * The owner and repository name. For example, Codertocat/Hello-World.
     **/
    repository_owner: string
    /**
     * The repository owner's name. For example, Codertocat.
     **/
    repositoryUrl: string
    /**
     * The Git URL to the repository. For example, git://github.com/codertocat/hello-world.git.
     **/
    retention_days: string
    /**
     * The number of days that workflow.ts run logs and artifacts are kept.
     **/
    /**
     * A unique number for each run of a particular workflow.ts in a repository. This number begins at 1 for the workflow.ts's
     * first run, and increments with each new run. This number does not change if you re-run the workflow.ts run.
     **/
    run_id: string
    /**
     * A unique number for each attempt of a particular workflow.ts run in a repository. This number begins at 1 for the
     * workflow.ts run's first attempt, and increments with each re-run.
     **/
    run_number: string
    /**
     * The source of a secret used in a workflow.ts. Possible values are None, Actions, Dependabot, or Codespaces.
     **/
    run_attempt: string
    /**
     * The URL of the GitHub server. For example: https://github.com.
     **/
    secret_source: string
    /**
     * The commit SHA that triggered the workflow.ts. The value of this commit SHA depends on the event that triggered the
     * workflow.ts. For more information, see "Events that trigger workflows."
     * For example, ffac537e6cbbf934b08745a378932722df287a53.
     **/
    server_url: string
    /**
     * A token to authenticate on behalf of the GitHub App installed on your repository. This is functionally equivalent
     * to the GITHUB_TOKEN secret. For more information, see "Automatic token authentication."
     **/
    sha: string
    /**
     * Note: This context property is set by the Actions runner, and is only available within the execution steps of a
     * job. Otherwise, the value of this property will be null.
     **/
    token: string
    /**
     * The username of the user that initiated the workflow.ts run. If the workflow.ts run is a re-run, this value may differ
     * from github.actor. Any workflow.ts re-runs will use the privileges of github.actor, even if the actor initiating the
     * re-run (github.triggering_actor) has different privileges.
     **/
    triggering_actor: string
    /**
     * The name of the workflow.ts. If the workflow.ts file doesn't specify a name, the value of this property is the full
     * path of the workflow.ts file in the repository.
     **/
    workflow: string
    /**
     * The default working directory on the runner for steps, and the default location of your repository when using
     * the checkout action.
     **/
    workspace: string
}

/**
 * This context changes for each job in a workflow.ts run. You can access this context from any step in a job. This object
 * contains all the properties listed below.
 **/
export interface Job {
    /**
     * Information about the job's container. For more information about containers, see "Workflow syntax for GitHub
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
     * for GitHub Actions."
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
    status: "success" | "failure" | "cancelled";
}

/**
 * This is only available in reusable workflows, and can only be used to set outputs for a reusable workflow.ts. This
 * object contains all the properties listed below.
 */
export type Jobs = {
    [jobId: string]: {
        /**
         * The result of a job in the reusable workflow.ts.
         */
        result: "success" | "failure" | "cancelled" | "skipped"
        /**
         * The set of outputs of a job in a reusable workflow.ts.
         */
        outputs: {
            [jobId: string]: {
                /**
                 * The value of a specific output for a job in a reusable workflow.ts.
                 */
                [outputName: string]: string
            }
        }
    }
}

/**
 * This context changes for each step in a job. You can access this context from any step in a job. This object contains all the properties listed below.
 */
export interface Steps {
    [stepId: string]: {
        /**
         * This context changes for each step in a job. You can access this context from any step in a job. This object contains all the properties listed below.
         * The set of outputs defined for the step. For more information, see "Metadata syntax for GitHub Actions."
         */
        outputs: {
            /**
             * The value of a specific output.
             */
            [outputId: string]: string
        }
        /**
         * The result of a completed step after continue-on-error is applied. Possible values are success, failure,
         * cancelled, or skipped. When a continue-on-error step fails, the outcome is failure, but the final conclusion
         * is success.
         */
        conclusion: "success" | "failure" | "skipped" | "cancelled"
        /**
         * The result of a completed step before continue-on-error is applied. Possible values are success, failure,
         * cancelled, or skipped. When a continue-on-error step fails, the outcome is failure, but the final conclusion
         * is success.
         */
        outcome: "success" | "failure" | "skipped" | "cancelled"
    }
}

/**
 * This context changes for each job in a workflow.ts run. This object contains all the properties listed below.
 */
export interface Runner {
    /**
     * The name of the runner executing the job.
     */
    name: string
    /**
     * The operating system of the runner executing the job.
     */
    os: string
    /**
     * The architecture of the runner executing the job.
     */
    arch: "Linux" | "Windows" | "manOS"
    /**
     * The path to a temporary directory on the runner. This directory is emptied at the beginning and end of each job.
     * Note that files will not be removed if the runner's user account does not have permission to delete them.
     */
    temp: "X86" | "X64" | "ARM" | "ARM64"
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
export interface Secrets {
    /**
     * The value of a specific secret.
     */
    [secretName: string]: string

    /**
     * Automatically created token for each workflow.ts run. For more information, see "Automatic token authentication."
     */
    GITHUB_TOKEN: string
}

/**
 * This context changes for each job in a workflow.ts run. You can access this context from any job or step in a workflow.ts.
 * This object contains all the properties listed below.
 */
export interface Strategy {
    /**
     * When true, all in-progress jobs are canceled if any job in a matrix fails. For more information, see "Workflow
     * syntax for GitHub Actions."
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
     * see "Workflow syntax for GitHub Actions."
     */
    maxParallel: number
}

/**
 * This context is only available for jobs in a matrix, and changes for each job in a workflow.ts run. You can access this
 * context from any job or step in a workflow.ts. This object contains the properties listed below.
 */
export interface Matrix {
    /**
     * The value of a matrix property.
     */
    [propertyName: string]: string
}

/**
 * This context is only populated for workflow.ts runs that have dependent jobs, and changes for each job in a workflow.ts run.
 * You can access this context from any job or step in a workflow.ts. This object contains all the properties listed below.
 */
export interface Needs {
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
        result: string
    }
}

/**
 * This context is only available in a reusable workflow.ts or in a workflow.ts triggered by the workflow_dispatch event.
 * You can access this context from any job or step in a workflow.ts. This object contains the properties listed below.
 */
export interface Inputs {
    /**
     * Each input value passed from an external workflow.ts.
     */
    [name: string]: string | number | boolean
}

export interface GithubActionsContext {
    github: Github
    /**
     * This context changes for each step in a job. You can access this context from any step in a job. This object
     * contains the properties listed below.
     */
    env: Dictionary<string>
    job: Job
    jobs: Jobs
    steps: Steps
    runner: Runner
    secrets: Secrets
    strategy: Strategy
    matrix: Matrix
    needs: Needs
    inputs: Inputs
}

export interface VentheActionsContext {
    internal: {
        /**
         * The full event webhook payload
         **/
        event: object
        /**
         * The default working directory on the runner for steps, and the default location of your repository when using
         * the checkout action.
         **/
        workspace: string
        binariesDirectory: string
        gerrit: {
            url: string
        }
        nexus: {
            url: string
        }
        docker: {
            url: string
        }
    }
    secrets: {
        /**
         * The value of a specific secret.
         */
        [secretName: string]: string | object

        nexus: {
            username: string
            password: string
        }
        docker: {
            username: string
            password: string
        }
    }
    env: Dictionary<string>
    addToPath: (path: string) => void
    addEnv: (env: string, value: string) => void
}

export type Context = GithubActionsContext & VentheActionsContext & any
