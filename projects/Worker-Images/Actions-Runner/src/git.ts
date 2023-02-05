import {shell, shellMany} from "./libraries/process";
import * as process from 'process'

export const configureGit = async () => {
    await shellMany([
        'git config --global init.defaultBranch main',
        'git config --global advice.detachedHead false',
        `git config --global --add safe.directory ${process.cwd()}`
    ])
}
