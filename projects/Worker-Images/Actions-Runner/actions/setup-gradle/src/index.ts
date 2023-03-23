import { shell } from '@pipeline/process';
import { callbacks, context, download, RepositoryType, unzip } from '@pipeline/core';
import fs from "fs";

(async function() {
  const filename = `${context.internal.binariesDirectory}/gradle.zip`;
  await download({
    sourcePath: 'java/gradle/gradle-7.6-all.zip', targetPath: filename, context: context, type: RepositoryType.System
  });
  await unzip(filename, context.internal.binariesDirectory);
  callbacks.addToPath(`${context.internal.binariesDirectory}/gradle-7.6/bin`);

  // To remove welcome message
  await shell(`gradle --version 2>/dev/null >/dev/null`);


  const initGradle = `
allprojects {
    buildscript {
        repositories {
            mavenLocal()
            maven { url "https://nexus.home.arpa/repository/maven-group/" }
        }
    }

    repositories {
        mavenLocal()
        maven { url "https://nexus.home.arpa/repository/maven-group/" }
    }
}`

  await shell(`mkdir .gradle || true`, {cwd: process.env.HOME})
  fs.writeFileSync(`${process.env.HOME}/.gradle/init.gradle`, initGradle);
})();
