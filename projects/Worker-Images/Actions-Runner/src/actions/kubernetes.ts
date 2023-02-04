import {Context, steps} from "../types";
import {download, RepositoryType} from "../libraries/artifacts";
import {shell} from "../libraries/process";

export const setupKubernetes = async (step: steps.Step, context: Context) => {
    await download({
        sourcePath: 'kubernetes/kubectl-linux-adm64-v1.26.0',
        targetFilename: `${context.binariesBase}/kubectl`,
        context,
        type: RepositoryType.System
    })
    await shell(`chmod +x ${context.binariesBase}/kubectl`)
}
