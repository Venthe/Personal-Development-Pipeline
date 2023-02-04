import {download, RepositoryType} from "../libraries/artifacts";
import {steps, VentheActionsContext} from "../types";

export const downloadArtifact = async (step: steps.ActionStep<{ filename: string, targetFilename: string }>, context: VentheActionsContext) =>
    await download({
        sourcePath: step.with?.filename || "",
        targetFilename: (step.with?.targetFilename ?? step.with?.filename?.split('/').pop()) || "",
        context: context,
        type: RepositoryType.System
    })
