import {steps, VentheActionsContext} from "../types";
import {download, RepositoryType} from "../libraries/artifacts";
import {shell} from "../libraries/process";

export const setupKubernetes = async (step: steps.Step, context: VentheActionsContext) => {
    await shell(`mkdir -p ${context.internal.binariesDirectory}/kubectl`)
    await download({
        sourcePath: 'kubernetes/kubectl-linux-adm64-v1.26.0',
        targetFilename: `${context.internal.binariesDirectory}/kubectl/kubectl`,
        context,
        type: RepositoryType.System
    })
    await shell(`chmod +x ${context.internal.binariesDirectory}/kubectl/kubectl`)
    context.addToPath(`${context.internal.binariesDirectory}/kubectl`)
    await shell('kubectl config set-context --current --namespace=default')
}
