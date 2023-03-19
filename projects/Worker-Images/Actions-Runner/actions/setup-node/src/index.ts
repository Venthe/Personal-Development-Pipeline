import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';
import fs from "fs";

type With = {
    version?: string,
};

function mapVersion(version?: number | string) {
    if (version === undefined) {
        return {
            targetArchive: "node.tar.xz",
            source: "node-v18.15.0-linux-x64.tar.xz",
            unpackedName: "node-v18.15.0-linux-x64"
        }
    }

    const _version: string = typeof version === "number" ? version.toString() : version;

    if (_version.startsWith("18")) {
        return {
            targetArchive: "node.tar.xz",
            source: "node-v18.15.0-linux-x64.tar.xz",
            unpackedName: "node-v18.15.0-linux-x64"
        }
    } else if (_version.startsWith("10")) {
      return {
          targetArchive: "node.tar.xz",
          source: "node-v10.18.0-linux-x64.tar.xz",
          unpackedName: "node-v10.18.0-linux-x64"
      }
    } else if (_version.startsWith("8")) {
        return {
            targetArchive: "node.tar.gz",
            source: "node-v8.17.0-linux-x64.tar.gz",
            unpackedName: "node-v8.17.0-linux-x64"
        }
    }

    throw new Error("Unsupported version")
}

(async function () {
    const _with = (step as ActionStepDefinition<With>)?.with ?? {};
    const {targetArchive, source, unpackedName} = mapVersion(_with.version)

    const filename = `${context.internal.binariesDirectory}/${targetArchive}`;
    await download({
        sourcePath: `node/${source}`,
        targetPath: filename,
        context: context,
        type: RepositoryType.System
    });
    await untar(filename, context.internal.binariesDirectory);
    callbacks.addToPath(`${context.internal.binariesDirectory}/${unpackedName}/bin`);

    const npmRc = `
unsafe-perm=true
allow-root=true
strict-ssl=false
sass-binary-site=https://nexus.home.arpa/repository/raw-hosted/npm/sass/node-sass/releases/download
//nexus.home.arpa/repository/npm-hosted/:_authToken=NpmToken.09d18dee-477e-380b-8205-fc0d16f54f0e
registry=https://nexus.home.arpa/repository/npm-group/`

    fs.writeFileSync(`${process.env.HOME}/.npmrc`, npmRc);
})();
