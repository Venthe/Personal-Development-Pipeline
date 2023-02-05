import * as fs from 'fs';
import * as yaml from 'js-yaml'
import {Dictionary, VentheActionsContext} from "./types";
import {shellMany} from "./libraries/process";
import * as process from "process";
import {SecretsManager} from "./libraries/secretsManager";
import * as envfile from "envfile"

/*
function getS() {
    try {
        return fs.readFileSync(`ci.env`, 'utf8');
    } catch (e) {
        return ""
    }
}

const supportedEnvironmentVariable = null as any
//     (() => {
//     console.log("Loading environment variable defaults")
//     const a = {
//         ...parse(getS().toString())
//     };
//     process.env = {...process.env,...a}
//     return a
// })()

const additionalEnv: { PATH: string[] } = {
    PATH: []
};

export const generateContext: () => Promise<Context> = async () => {
    console.log(JSON.stringify(supportedEnvironmentVariable))

    //ensure directories
    await shell(`
    mkdir -p ${supportedEnvironmentVariable.RUNNER_WORKDIR} && \
    mkdir -p ${supportedEnvironmentVariable.RUNNER_MANAGER_DIRECTORY} && \
    mkdir -p ${supportedEnvironmentVariable.RUNNER_METADATA_DIRECTORY} && \
    mkdir -p ${supportedEnvironmentVariable.RUNNER_BINARIES_DIRECTORY} && \
    mkdir -p ${supportedEnvironmentVariable.RUNNER_CACHE_DIRECTORY}
    `)

    const ctx: any = {
        workdir: supportedEnvironmentVariable.RUNNER_WORKDIR,
        binariesBase: supportedEnvironmentVariable.RUNNER_BINARIES_DIRECTORY,
        runner: {
            type: os.type(),
            platform: os.platform(),
            release: os.release(),
            architecture: os.arch(),
            userInfo: os.userInfo(),
            tmpdir: os.tmpdir(),
            machine: os.machine(),
        },
        links: {
            nexus.json: supportedEnvironmentVariable.NEXUS_URL,
            gerrit: supportedEnvironmentVariable.GERRIT_SSH
        }
    };

    const workflowFile = fs.readFileSync(`${supportedEnvironmentVariable.RUNNER_METADATA_DIRECTORY}/${supportedEnvironmentVariable.PIPELINE_WORKFLOW}`, 'utf8').toString();
    const templatedWorkflowFile = yaml.load(render(workflowFile, ctx))

    const newCtx: Context2 | any = {...ctx, workflow: templatedWorkflowFile};

    newCtx.addToPath = addToPath
    newCtx.addEnv = addEnv

    return newCtx;
}

export const updateEnv = async (ctx: Context2 & any, stepName) => {
    (process as any).env = {
        ...process.env,
        ...supportedEnvironmentVariable,
        ...(ctx?.input?.env || {}),
        ...(ctx?.workflow?.env || {}),
        ...((ctx?.workflow?.jobs as any)[supportedEnvironmentVariable.PIPELINE_JOB_NAME as string]?.env || {}),
        ...(((ctx?.workflow?.jobs as any)[supportedEnvironmentVariable.PIPELINE_JOB_NAME as string]?.steps as any)[stepName]?.env || {}),
        ...additionalEnv,
        PATH: process.env.PATH = [...new Set([
            ...additionalEnv.PATH,
            ctx.binariesBase,
            ...(process.env.PATH?.split(":") || [])
        ])].join(":")
    }
}

export const addToPath = (path: string) => {
    additionalEnv.PATH.push(path)
}

export const addEnv = (env, value) => {
    if (env?.toLowerCase() === "PATH".toLowerCase())
        throw Error()

    additionalEnv[env] = value;
}

export const getJob = (context) => {
    return context?.workflow?.jobs[supportedEnvironmentVariable.PIPELINE_JOB_NAME as string]
}
*/

export interface ContextEnvironmentVariables {
    [key: string]: string | undefined

    VPIPELINE_BINARIES_DIRECTORY: string
    VPIPELINE_JOB_NAME: string
    VPIPELINE_BUILD_ID: string
    VPIPELINE_RUNNER_METADATA_DIRECTORY: string
    VPIPELINE_WORKFLOW: string
    VPIPELINE_RUNNER_CACHE_DIRECTORY: string
    VPIPELINE_RUNNER_MANAGER_DIRECTORY: string
    VPIPELINE_RUNNER_SECRETS_DIRECTORY: string
    VPIPELINE_RUNNER_ENV_DIRECTORY: string
    VPIPELINE_NEXUS_URL: string
    VPIPELINE_GERRIT_URL: string
    VPIPELINE_DOCKER_URL: string
}

export class Context {
    private readonly environmentVariables: ContextEnvironmentVariables;
    private workflowFile: any;
    private readonly event: any;
    private secrets: object = {};

    constructor(environmentVariables: ContextEnvironmentVariables, private readonly secretsManager: SecretsManager) {
        // console.log(environmentVariables)
        this.environmentVariables = environmentVariables

        this.event = yaml.load(fs.readFileSync(`${this.environmentVariables.VPIPELINE_RUNNER_METADATA_DIRECTORY}/event.yaml`, 'utf8').toString());
    }

    async postConstruct() {
        const opts = {cwd: this.environmentVariables.VPIPELINE_RUNNER_METADATA_DIRECTORY}
        await shellMany([
            `git init .`,
            `git fetch --tags --force --progress --depth=1 -- ${this.environmentVariables.VPIPELINE_GERRIT_URL}/${this.event.change.project} '+refs/heads/*:refs/remotes/origin/*'`,
            `git config remote.origin.url ${this.environmentVariables.VPIPELINE_GERRIT_URL}/${this.event.change.project}`,
            `git config --add remote.origin.fetch +refs/heads/*:refs/remotes/origin/*`,
            `git fetch --tags --force --progress --depth=1 -- ${this.environmentVariables.VPIPELINE_GERRIT_URL}/${this.event.change.project} '${this.event.patchSet.ref}'`,
            `git sparse-checkout set ".pipeline/" --no-cone`,
            `git checkout FETCH_HEAD`
        ], opts)
        this.secrets = this.secretsManager.retrieve();

        this.workflowFile = yaml.load(fs.readFileSync(`${this.environmentVariables.VPIPELINE_RUNNER_METADATA_DIRECTORY}/${this.environmentVariables.VPIPELINE_WORKFLOW}`, 'utf8').toString());
    }

    public static async create(environmentVariables: ContextEnvironmentVariables): Promise<Context> {
        environmentVariables.VPIPELINE_RUNNER_CACHE_DIRECTORY = "/runner/cache"
        environmentVariables.VPIPELINE_RUNNER_MANAGER_DIRECTORY = "/runner"
        environmentVariables.VPIPELINE_RUNNER_METADATA_DIRECTORY = "/runner/metadata"
        environmentVariables.VPIPELINE_BINARIES_DIRECTORY = "/runner/bin"
        environmentVariables.VPIPELINE_RUNNER_SECRETS_DIRECTORY = "/runner/metadata/secrets"
        environmentVariables.VPIPELINE_RUNNER_ENV_DIRECTORY = "/runner/metadata/env"

        Context.loadEnvFiles(environmentVariables.VPIPELINE_RUNNER_ENV_DIRECTORY);

        const secretsManager = SecretsManager.create(environmentVariables);
        const context = new Context(environmentVariables, secretsManager);
        await context.postConstruct();

        return Promise.resolve(context)
    }

    private static loadEnvFiles(VPIPELINE_RUNNER_ENV_DIRECTORY: string) {
        const files = fs.readdirSync(VPIPELINE_RUNNER_ENV_DIRECTORY)
        files.forEach(file => {
            let filepath = `${VPIPELINE_RUNNER_ENV_DIRECTORY}/${file}`
            let loadedFile = fs.readFileSync(filepath, "utf-8");
            let result = envfile.parse(loadedFile)
            Object.keys(result).forEach((key) => {
                process.env[key] = result[key]
            })
        });
    }

    public get buildId() {
        return this.environmentVariables.VPIPELINE_BUILD_ID;
    }

    public get binariesDirectory(): string {
        return this.environmentVariables.VPIPELINE_BINARIES_DIRECTORY
    }

    addToPath(newPath: string) {
        process.env.PATH = `${newPath}:${process.env.PATH}`
    }

    addEnv(env: string, s: string) {
        process.env[env] = s
    }

    getJob(): any {
        return this.workflowFile["jobs"][this.environmentVariables.VPIPELINE_JOB_NAME]
    }

    async getCtx(): Promise<VentheActionsContext> {
        return {
            env: {...process.env} as Dictionary<string>,
            internal: {
                event: this.event,
                workspace: process.cwd(),
                gerrit: {
                    url: this.environmentVariables.VPIPELINE_GERRIT_URL
                },
                nexus: {
                    url: this.environmentVariables.VPIPELINE_NEXUS_URL
                },
                docker: {
                    url: this.environmentVariables.VPIPELINE_DOCKER_URL
                },
                binariesDirectory: this.environmentVariables.VPIPELINE_BINARIES_DIRECTORY
            },
            secrets: {
                ...(this.secrets as any)
            },
            addEnv: this.addEnv,
            addToPath: this.addToPath
        }
    }
}
