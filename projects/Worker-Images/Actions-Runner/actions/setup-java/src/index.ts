import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';
import {shell} from "@pipeline/process";

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

    let v;
    switch (implementation) {
        case "corretto":
            v = mapCorrettoVersion(type, _with.version)
            await setupJava(type, `amazon-corretto-${v}-linux-x64.tar.gz`, `amazon-corretto-${v}-linux-x64/bin`, `amazon-corretto-${v}-linux-x64`);
            break
        case "azul":
            v = mapAzulVersion(type, _with.version)
            await setupJava(type, `zulu${v}-linux_x64.tar.gz`, `zulu${v}-linux_x64/bin`, `zulu${v}-linux_x64`);
            break
        default:
            throw new Error(`Unsupported implementation: ${type}/${implementation}`)
    }

    // TODO: Mask password; use from secrets
    const {version: versionString} = v.match(/^(?<version>\d+)\D?.*$/)?.groups || {}
    if (+versionString <= 5) {
        await shell(`keytool -import -trustcacerts -noprompt -storepass changeit -file /certs/ca.crt -keystore ${process.env.JAVA_HOME}/jre/lib/security/cacerts -alias "K8s"`)
    } else if (+versionString <= 8) {
        await shell(`keytool -importcert -trustcacerts -noprompt -storepass changeit -file /certs/ca.crt -keystore ${process.env.JAVA_HOME}/jre/lib/security/cacerts -alias "K8s"`)
    } else {
        await shell('keytool -importcert -trustcacerts -noprompt -storepass changeit -cacerts -file /certs/ca.crt -alias "K8s"')
    }
})();
