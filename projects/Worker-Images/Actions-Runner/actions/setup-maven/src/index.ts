import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';

type With = {
    version?: string,
};

function toString(str?: string | number) {
    if (!str) {
        return ""
    }
    return typeof str === "number" ? str.toString() : str;
}

function mapVersion() {
    let _with = (step as ActionStepDefinition<With>)?.with ?? {};
    if (_with.version === undefined) {
        return "3.9.0"
    }

    if (toString(_with.version).startsWith("3")) {
        return "3.3.9"
    }

    throw new Error("Unsupported version")
}

(async function () {
    const version: string = mapVersion()
    const filename = `${context.internal.binariesDirectory}/maven.tar.gz`;
    await download({
        sourcePath: `java/maven/apache-maven-${version}-bin.tar.gz`,
        targetPath: filename,
        context: context,
        type: RepositoryType.System
    });
    await untar(filename, context.internal.binariesDirectory);
    callbacks.addToPath(`${context.internal.binariesDirectory}/apache-maven-${version}/bin`);
})();
