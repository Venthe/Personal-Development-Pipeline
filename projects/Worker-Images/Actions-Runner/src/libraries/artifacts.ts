import * as fs from 'fs';
import * as http from "http";
import {password, shell} from "./process";
import {SecretsManager} from "./secretsManager";
import {Context} from "../configuration";
import {VentheActionsContext} from "../types";

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
    let usernamePassword = `${context.secrets.NEXUS_USERNAME}:${context.secrets.NEXUS_PASSWORD}`;
    let url = `${context.internal.nexus.url}/repository/raw/${ap}`;
    return shell(`curl --silent --fail --basic -u ${usernamePassword} --request GET ${url} > ${targetFilename}`, {mask: [password("-u")]});
}

export const upload = async ({
                                 sourcePath,
                                 targetPath,
                                 context,
                                 type = RepositoryType.User
                             }: { sourcePath: string, targetPath: string, type: RepositoryType, context: VentheActionsContext }) =>
    new Promise<number>(async (resolve, fail) => {
        const ap = pathStrategy(context, targetPath ?? sourcePath, type)
        console.debug("Uploading artifact", ap)
        fs.createReadStream(sourcePath).pipe(http.request((context as any).links.nexus, {
            headers: {
                Authorization: 'Basic ' + Buffer.from(`${context.secrets.NEXUS_USERNAME}:${context.secrets.NEXUS_PASSWORD}`).toString('base64')
            },
            path: `/repository/raw/${ap}`,
            method: 'PUT',
        }, response => {
            // response.on('close', () => {
            //     console.debug("close");
            // });
            // response.on('data', (chunk) => {
            //     console.debug("data", chunk);
            // });
            // response.on('end', () => {
            //     console.debug("end");
            // });
            response.on('error', (err) => {
                console.debug("error", err);
                fail(err);
            });
            // response.on('pause', () => {
            //     console.debug("pause");
            // });
            response.on('readable', () => {
                console.debug("readable");
                response.statusCode === 201 ? resolve(response.statusCode) : fail(response.statusCode);
            });
            // response.on('resume', () => {
            //     console.debug("resume");
            // });
        }));
    })

const pathStrategy = (context: VentheActionsContext, sourcePath, type: RepositoryType) => {
    let event: any = context.internal.event;
    return (type === RepositoryType.User ? `${["pipeline", event.change.project, event.change.number, event.patchSet.number, sourcePath].join("/")}` : `${sourcePath}`);
}
