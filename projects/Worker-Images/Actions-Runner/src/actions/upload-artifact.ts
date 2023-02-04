import {steps, VentheActionsContext} from "../types";
import {RepositoryType, upload} from "../libraries/artifacts";
import {ActionStep} from "../types/steps";

export const uploadArtifact = async (step: steps.ActionStep<{ path: string, targetPath: string }>, context: VentheActionsContext) =>
    await upload({
        sourcePath: step.with?.path ?? "",
        targetPath: step.with?.targetPath ?? "",
        context: context,
        type: RepositoryType.User
    })
