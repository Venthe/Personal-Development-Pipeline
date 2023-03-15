import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';
import {ActionStepDefinition} from '@pipeline/types';
import {shell, shellMany} from "@pipeline/process";
import * as fs from "fs";

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

    const settingsXml = `
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">

  <servers>
    <server>
      <id>nexus-snapshots</id>
      <username>${context.secrets.NEXUS_USERNAME}</username>
      <password>${context.secrets.NEXUS_PASSWORD}</password>
    </server>
    <server>
      <id>nexus-releases</id>
      <username>${context.secrets.NEXUS_USERNAME}</username>
      <password>${context.secrets.NEXUS_PASSWORD}</password>
    </server>
  </servers>

  <mirrors>
    <mirror>
      <id>central</id>
      <name>central</name>
      <url>https://nexus.home.arpa/repository/maven-group/</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>

</settings>`

    await shell(`mkdir .m2`, {cwd: process.env.HOME})
    fs.writeFileSync(`${process.env.HOME}/.m2/settings.xml`, settingsXml);
 })();
