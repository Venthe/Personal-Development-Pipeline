import {steps, VentheActionsContext} from "../types";
import {download, RepositoryType} from "../libraries/artifacts";
import {untar} from "../libraries/compression";

export const setupJava = async (step: steps.ActionStep, ctx: VentheActionsContext) => {
    const filename = `${ctx.internal.binariesDirectory}/java.tar.gz`
    await download({
        sourcePath: 'java/jdk/zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz',
        targetFilename: filename,
        context: ctx,
        type: RepositoryType.System
    })
    await untar(filename, ctx.internal.binariesDirectory)
    ctx.addToPath(`${ctx.internal.binariesDirectory}/zulu17.38.21-ca-jdk17.0.5-linux_x64/bin`)
    ctx.addEnv("JAVA_HOME", `${ctx.internal.binariesDirectory}/zulu17.38.21-ca-jdk17.0.5-linux_x64`)
}
