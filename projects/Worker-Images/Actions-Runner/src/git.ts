import {shell} from "./libraries/process";

export const configureGit = async () => {
    await shell(`git config --global init.defaultBranch main`)
    await shell('git config --global advice.detachedHead false')
}
