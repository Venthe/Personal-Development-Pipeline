import * as fs from 'fs';
import * as http from "http";
import {password, shell} from "./process";
import {SecretsManager} from "./secretsManager";
import {Context} from "../configuration";
import {VentheActionsContext} from "../types";
import * as https from "https";

export enum RepositoryType {
    User,
    System
}

export const download = async ({
                                   sourcePath,
                                   targetFilename,
                                   context,
                                   type = RepositoryType.User
                               }: { sourcePath: string, targetFilename: string, type: RepositoryType, context: VentheActionsContext }) => {
    const ap = pathStrategy(context, sourcePath, type)
    console.debug("Downloading artifact", ap, "to", targetFilename)
    let usernamePassword = `${context.secrets.nexus.username}:${context.secrets.nexus.password}`;
    let url = `${context.internal.nexus.url}/${ap}`;
    return shell(`curl --fail --user ${usernamePassword} --request GET ${url} > ${targetFilename}`, {mask: [password("--user")]});
}

export const upload = async ({
                                 sourcePath,
                                 targetPath,
                                 context,
                                 type = RepositoryType.User
                             }: { sourcePath: string, targetPath: string, type?: RepositoryType, context: VentheActionsContext }) => {
    const ap = pathStrategy(context, targetPath ?? sourcePath, type)
    console.debug("Uploading artifact", sourcePath, "to", ap)
    let usernamePassword = `${context.secrets.nexus.username}:${context.secrets.nexus.password}`;
    let url = `${context.internal.nexus.url}/${ap}`;

    return shell(`curl --fail --user ${usernamePassword} --upload-file "${sourcePath}" "${url}"`, {mask: [password("--user")]})
}

const pathStrategy = (context: VentheActionsContext, sourcePath, type?: RepositoryType) => {
    let event: any = context.internal.event;
    return (type === RepositoryType.User ? `${["pipeline", event.change.project, event.change.number, event.patchSet.number, sourcePath].join("/")}` : `${sourcePath}`);
}
