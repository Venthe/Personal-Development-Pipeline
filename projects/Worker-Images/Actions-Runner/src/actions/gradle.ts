import {download, RepositoryType} from "../libraries/artifacts";
import {unzip} from "../libraries/compression";
import {steps, VentheActionsContext} from "../types";
import {shell} from "../libraries/process";

export const setupGradle = async (step: steps.ActionStep, ctx: VentheActionsContext) => {
    const filename = `${ctx.internal.binariesDirectory}/gradle.zip`
    await download({
        sourcePath: 'gradle/gradle-7.6-all.zip', targetFilename: filename, context: ctx, type: RepositoryType.System
    })
    await unzip(filename, ctx.internal.binariesDirectory)
    ctx.addToPath(`${ctx.internal.binariesDirectory}/gradle-7.6/bin`)

    // To remove welcome message
    await shell(`gradle --version`)
}
