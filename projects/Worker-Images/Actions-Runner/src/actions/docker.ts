import {password as passwordRegexp, shell} from "../libraries/process";
import {steps, VentheActionsContext} from "../types";
import {download, RepositoryType} from "../libraries/artifacts";

type Docker = { url?: string, username?: string, password?: string };

export const setupDocker = async (step: steps.ActionStep<Docker>, context: VentheActionsContext) => {
    await download({
        sourcePath: 'docker/nerdctl-1.1.0-linux-amd64.tar.gz',
        targetFilename: `${context.internal.binariesDirectory}/nerdctl-1.1.0-linux-amd64.tar.gz`,
        context,
        type: RepositoryType.System
    })
    await shell(`
    tar xzvf ${context.internal.binariesDirectory}/nerdctl-1.1.0-linux-amd64.tar.gz && \
    mkdir -p ${context.internal.binariesDirectory}/nerdctl && \
    mv nerdctl ${context.internal.binariesDirectory}/nerdctl && \
    rm -r ${context.internal.binariesDirectory}/nerdctl-1.1.0-linux-amd64.tar.gz
    `)
    // ln -s ${context.internal.binariesDirectory}/nerdctl/nerdctl ${context.internal.binariesDirectory}/nerdctl/docker && \
    context.addToPath(`${context.internal.binariesDirectory}/nerdctl`)
}

export const dockerLogin = async (step: steps.ActionStep<Docker>, context: VentheActionsContext) => {
    const url = step.with?.url || context.internal.docker.url;
    const username = step.with?.username ?? context.secrets.DOCKER_USERNAME
    const password = context.secrets[step.with?.password ?? ""] ?? context.secrets.DOCKER_PASSWORD
    await shell(`echo ${password} | nerdctl login ${url ?? ""} --username ${username} --password-stdin --insecure-registry`, {mask: [passwordRegexp("echo")]})
}
