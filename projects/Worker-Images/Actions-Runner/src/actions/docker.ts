import {password as passwordRegexp, shell} from "../libraries/process";
import {steps, VentheActionsContext} from "../types";

type Docker = { url?: string, username?: string, password?: string };

export const setupDocker = async (step: steps.ActionStep<Docker>, context: VentheActionsContext) => {
    context.addEnv("DOCKER_HOST", "tcp://docker.infrastructure:2375")
}

export const dockerLogin = async (step: steps.ActionStep<Docker>, context: VentheActionsContext) => {
    const url = step.with?.url || context.internal.docker.url;
    const username = step.with?.username ?? context.secrets.docker.username
    const password = context.secrets[step.with?.password ?? ""] ?? context.secrets.docker.password
    await shell(`echo ${password} | docker login ${url ?? ""} --username ${username} --password-stdin`, {mask: [passwordRegexp("echo")]})
}
