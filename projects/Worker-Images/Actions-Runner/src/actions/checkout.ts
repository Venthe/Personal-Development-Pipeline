import {shell, shellMany} from "../libraries/process";
import {steps, VentheActionsContext} from "../types";

export const checkout = async (step: steps.ActionStep<{ type?: string }>, context: VentheActionsContext) => {
    const repositoryType = step.with?.type ?? "GERRIT"

    if (repositoryType === "GITHUB") {
        console.log("Downloading from Github")

        await shell(`git clone https://github.com/Venthe/test_project.git ${context.internal.workspace}`)
    }

    if (repositoryType === "GERRIT") {
        const gerritURL = context.internal.gerrit.url;
        const event: any = context.internal.event;
        console.log("Downloading from Gerrit")
        await shellMany([
            `git init`,
            `git fetch --tags --force --progress --depth=1 -- ${gerritURL}/${event.change.project} '+refs/heads/*:refs/remotes/origin/*'`,
            `git config remote.origin.url ${gerritURL}/${event.change.project}`,
            `git config --add remote.origin.fetch '+refs/heads/*:refs/remotes/origin/*'`,
            `git config remote.origin.url ${gerritURL}/${event.change.project}`,
            `git fetch --tags --force --progress --depth=1 -- ${gerritURL}/${event.change.project} '${event.patchSet.ref}'`,
            `git checkout -f FETCH_HEAD -b change-${event.change.number}`
        ]);
    }
}
