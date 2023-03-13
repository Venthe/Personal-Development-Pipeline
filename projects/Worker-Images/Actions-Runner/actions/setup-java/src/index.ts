import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';

// TODO: Use nexus API to list java binaries

type With = {
    version?: string,
    implementation?: "azul" | "corretto" | string,
    type: "jdk" | "sdk"
};

const setBin = path => {
    callbacks.addToPath(`${context.internal.binariesDirectory}/${path}`);
};

const setJavaHome = path => {
    callbacks.addEnv('JAVA_HOME', `${context.internal.binariesDirectory}/${path}`);
};

async function setupJava(type, _filename, binPath, javaHome) {
    const filename = `${context.internal.binariesDirectory}/java.tar.gz`;
    await download({
        sourcePath: `java/${type}/${_filename}`,
        targetPath: filename,
        context: context,
        type: RepositoryType.System
    });
    await untar(filename, context.internal.binariesDirectory);
    setBin(binPath);
    setJavaHome(javaHome)
}

const mapCorrettoVersion = (type: string, version?: string) => {
    if (type !== "jdk")
        throw new Error(`JRE not supported`)

    if (version === undefined || version?.startsWith("8")) {
        return "8.362.08.1"
    } else {
        throw new Error(`Unsupported amazon corretto version ${version}`)
    }
}

const mapAzulVersion = (type: string, version?: string) => {
    if (type !== "jdk")
        throw new Error(`JRE not supported`)

    if (version === undefined || version?.startsWith("17")) {
        return "17.38.21-ca-jdk17.0.5"
    } else {
        throw new Error(`Unsupported azul version ${version}`)
    }
}

(async function () {
    const _with: With = (step as ActionStepDefinition<With>).with ?? ({} as With)
    const type: string = _with.type ?? "jdk"
    let implementation: string = _with.implementation ?? "azul"

    // Hack
    if (_with.version !== undefined && _with.version.startsWith("8")) {
        implementation = "corretto"
    }

    switch (implementation) {
        case "corretto":
            const correttoVersion = mapCorrettoVersion(type, _with.version)
            await setupJava(type, `amazon-corretto-${correttoVersion}-linux-x64.tar.gz`, `amazon-corretto-${correttoVersion}-linux-x64/bin`, `amazon-corretto-${correttoVersion}-linux-x64`);
            break
        case "azul":
            const azulVersion = mapAzulVersion(type, _with.version)
            await setupJava(type, `zulu${azulVersion}-linux_x64.tar.gz`, `zulu${azulVersion}-linux_x64/bin`, `zulu${azulVersion}-linux_x64`);
            break
        default:
            throw new Error(`Unsupported implementation: ${type}/${implementation}`)
    }
})();
