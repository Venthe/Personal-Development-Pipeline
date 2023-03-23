const {mkdtempSync, rmSync, writeFileSync, readdirSync, copyFileSync, cpSync, readFileSync, mkdirSync} = require("fs");
const {join} = require("path");
const {tmpdir} = require("os");
const {randomString} = require("../utilities");
const {dockerCommand} = require("docker-cli-js");
const {GenericContainer} = require("testcontainers");
const {spawn} = require("child_process");
const yaml = require('js-yaml');

const timeout = 300000

let context

function setupJavaSteps(_with = {}, command = "-version") {
    return [
        {uses: "actions/setup-java@main", ...(_with ? {with: _with} : {})},
        {run: `java ${command}`}
    ];
}

function assertFilter(log, newParam) {
    expect(log.filter(newParam)[0]).toBeTruthy();
}

describe("Tests", () => {
    describe("Services", () => {
        it("basic", () => test({
            given: () => {
                context.steps = [{run: 'curl localhost:5050/foo/bar'}]
                context.job = {
                    services: {
                        helloworld: {
                            image: 'vad1mo/hello-world-rest',
                            ports: ["5050:5050"]
                        }
                    }
                }
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log.filter(l => l.includes("/:path1/:path2 - Hello to foo/bar !"))[0]).toBeTruthy();
            }
        }), timeout)
        it("with mount", () => test({
            given: () => {
                context.steps = [{run: 'echo noop'}]
                context.job = {
                    services: {
                        test: {
                            image: 'alpine',
                            command: ['cat', '/workdir/test'],
                            volumes: [`/runner/runner.sh:/workdir/test`]
                        }
                    }
                }
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                assertFilter(log, line => line.includes("node /runner/index.js ${@}"));
            }
        }), timeout)
    })
    describe("Actions", () => {
        describe("setup-node", () => {
            it("latest", () => test({
                given: () => {
                    context.steps = [{uses: "actions/setup-node@main"}, {run: 'npm --version'}, {run: 'node --version'}, {run: 'npx --version'}]
                    withNexusSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("9.5.0"));
                    assertFilter(log, line => line.includes("v18.15.0"));
                    assertFilter(log, line => line.includes("9.5.0"));
                }
            }), timeout)
            it("with-version: 10", () => test({
                given: () => {
                    context.steps = [{
                        uses: "actions/setup-node@main",
                        with: {version: "10"}
                    }, {run: 'npm --version'}, {run: 'node --version'}, {run: 'npx --version'}]
                    withNexusSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("6.13.4"));
                    assertFilter(log, line => line.includes("v10.18.0"));
                    assertFilter(log, line => line.includes("6.13.4"));
                }
            }), timeout)
            it("with-version: 8", () => test({
                given: () => {
                    context.steps = [{
                        uses: "actions/setup-node@main",
                        with: {version: "8"}
                    }, {run: 'npm --version'}, {run: 'node --version'}, {run: 'npx --version'}]
                    withNexusSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("6.13.4"));
                    assertFilter(log, line => line.includes("v8.17.0"));
                    assertFilter(log, line => line.includes("6.13.4"));
                }
            }), timeout)
        })
        describe("docker", () => {
            it("docker login", () => test({
                given: () => {
                    context.steps = [{uses: "actions/setup-docker@main"}, {uses: "actions/docker-login@main"}]
                    withDockerSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("Login Succeeded"));
                }
            }), timeout)
            it("setup-docker", () => test({
                given: () => {
                    context.directory = "actions/setup-docker"
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("Works!"));
                }
            }), timeout)
        })
        xit("deploy", () => test({
            given: () => {
                context.steps = [{uses: "actions/deploy@main"}]
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
            }
        }), timeout)
        it("setup-helm", () => test({
            given: () => {
                context.steps = [{uses: "actions/setup-helm@main"}, {run: 'helm version'}]
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                assertFilter(log, line => line.includes("version.BuildInfo{Version:\"v3.11.0\", GitCommit:\"472c5736ab01133de504a826bd9ee12cbe4e7904\", GitTreeState:\"clean\", GoVersion:\"go1.18.10\"}"));
            }
        }), timeout)
        it("checkout", () => test({
            given: () => {
                context.directory = "actions/checkout"
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                assertFilter(log, line => line.includes("regular empty file"));
            }
        }), timeout)
        it("setup-kubectl", () => test({
            given: () => {
                context.steps = [{uses: "actions/setup-kubectl@main"}, {run: 'kubectl version'}]
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                assertFilter(log, line => line.includes("Client Version: version.Info{Major:\"1\", Minor:\"26\", GitVersion:\"v1.26.0\", GitCommit:\"b46a3f887ca979b1a5d14fd39cb1af43e7e5d12d\", GitTreeState:\"clean\", BuildDate:\"2022-12-08T19:58:30Z\", GoVersion:\"go1.19.4\", Compiler:\"gc\", Platform:\"linux/amd64\"}"));
                assertFilter(log, line => line.includes("Kustomize Version: v4.5.7"));
            }
        }), timeout)
        describe("Python", () => {
            describe("PIP", () => {
                it("requirements", () => test({
                    given: () => {
                        context.directory = "actions/python/pip/requirements"
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        assertFilter(log, line => line.includes("Hello world!"));
                    }
                }), timeout)
                it("with", () => test({
                    given: () => {
                        context.directory = "actions/python/pip/with"
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        assertFilter(log, line => line.includes("Hello world!"));
                    }
                }), timeout)
            })
            it("setup-python", () => test({
                given: () => {
                    context.steps = [{uses: "actions/setup-python@main"}, {run: 'python3 --version'}]
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("Python 3.8.0"));
                }
            }), timeout)
        })
        it("setup-yq", () => test({
            given: () => {
                context.directory = "actions/setup-yq"
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log).toContainEqual("value: 1");
            }
        }), timeout)
        it("template", () => test({
            given: () => {
                context.steps = [{uses: "actions/template@main"}, {run: 'printenv'}, {run: 'echo "${{ steps.step_1.outputs | stringify }}"'}]
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log.filter(l => l.includes("Console.log: Hello world!"))[0]).toBeTruthy();
                expect(log).toContainEqual("/workdir");
                expect(log).toContainEqual("CHILD_ENV=123");
                expect(log).toContainEqual("{sample:\"a\":1} {sample:\"b\":2}");
            }
        }), timeout)
        describe("Java", () => {
            describe("Maven", () => {
                it("settings", async () => test({
                    given: () => {
                        context.steps = [{
                            uses: "actions/setup-maven@main",
                            with: {version: "3.3"}
                        }, {run: "ls ~/.m2 && cat ~/.m2/settings.xml"}]
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        expect(log).toContainEqual("settings.xml");
                        expect(log).toContainEqual("      <url>https://nexus.home.arpa/repository/maven-group/</url>");
                        expect(log).toContainEqual("      <id>nexus-releases</id>");
                        expect(log).toContainEqual("      <id>nexus-snapshots</id>");
                    }
                }), timeout)
                it("with-version", async () => test({
                    given: () => {
                        context.steps = [...setupJavaSteps({version: "8"}), {
                            uses: "actions/setup-maven@main",
                            with: {version: "3.3"}
                        }, {run: "mvn --version"}]
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        expect(log).toContainEqual("Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T16:41:47+00:00)");
                        expect(log).toContainEqual("Maven home: /runner/bin/apache-maven-3.3.9");
                    }
                }), timeout)
                it("default", async () => test({
                    given: () => {
                        context.steps = [...setupJavaSteps(), {uses: "actions/setup-maven@main"}, {run: "mvn --version"}]
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        expect(log).toContainEqual("Apache Maven 3.9.0 (9b58d2bad23a66be161c4664ef21ce219c2c8584)");
                        expect(log).toContainEqual("Maven home: /runner/bin/apache-maven-3.9.0");
                    }
                }), timeout)
            })
            describe("gradle", () => {
                it("default", async () => test({
                    given: () => {
                        context.steps = [...setupJavaSteps(), {uses: "actions/setup-gradle@main"}, {run: "gradle --version"}]
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        expect(log).toContainEqual("Gradle 7.6");
                        expect(log).toContainEqual("Revision:     daece9dbc5b79370cc8e4fd6fe4b2cd400e150a8");
                    }
                }), timeout)
                it("settings", async () => test({
                    given: () => {
                        context.steps = [...setupJavaSteps(), {uses: "actions/setup-gradle@main"}, {run: `cat /root/.gradle/init.gradle`}]
                        withNexusSecret()
                    },
                    then: ({result, log}) => {
                        expect(result).toBe("success");
                        expect(log).toContainEqual("            maven { url \"https://nexus.home.arpa/repository/maven-group/\" }");
                        expect(log).toContainEqual("        maven { url \"https://nexus.home.arpa/repository/maven-group/\" }");
                    }
                }), timeout)
            })
            it("Java 8", async () => test({
                given: () => {
                    context.steps = setupJavaSteps({version: "8", implementation: "corretto"})
                    withNexusSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("openjdk version \"1.8.0_362\""));
                    assertFilter(log, line => line.includes("OpenJDK Runtime Environment Corretto-8.362.08.1 (build 1.8.0_362-b08)"));
                    assertFilter(log, line => line.includes("OpenJDK 64-Bit Server VM Corretto-8.362.08.1 (build 25.362-b08, mixed mode)"));
                }
            }), timeout)
            it("Default", async () => test({
                given: () => {
                    context.steps = setupJavaSteps()
                    withNexusSecret()
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    assertFilter(log, line => line.includes("openjdk version \"17.0.5\" 2022-10-18 LTS"));
                    assertFilter(log, line => line.includes("OpenJDK Runtime Environment Zulu17.38+21-CA (build 17.0.5+8-LTS)"));
                    assertFilter(log, line => line.includes("OpenJDK 64-Bit Server VM Zulu17.38+21-CA (build 17.0.5+8-LTS, mixed mode, sharing)"));
                }
            }), timeout)
        })
        it("Artifact", async () => test({
            given: () => {
                context.directory = "actions/artifact"
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log).toContainEqual("Hello");
            }
        }))
    })
    describe("Common", () => {
        it('call-shell', async () => test({
            given: () => {
                context.directory = "call-shell"
                withDockerSecret()
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log).toContainEqual("integration: Works!");
            }
        }), timeout)
        it('composite-and-custom', async () => test({
            given: () => {
                context.directory = "composite-and-custom"
                withDockerSecret()
                withNexusSecret()
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log).toContainEqual("Hello Mona the Octocat.");
                expect(log).toContainEqual("/workdir");
                expect(log).toContainEqual("Hello world!");

                assertFilter(log, line => line.match(/^> echo \d+$/));
                assertFilter(log, line => line.match(/^\d+$/));
            }
        }), timeout)
        it('inputs', async () => test({
            given: () => {
                context.event = {
                    additionalProperties: {
                        inputs: {
                            test: 1,
                            test2: "2",
                            test4: false
                        }
                    }
                }
                context.steps = [
                    {run: "echo ${{ inputs.test }}"},
                    {run: "echo ${{ inputs.test2 }}"},
                    {run: "echo ${{ inputs.test4 }}"}
                ]
            },
            then: ({result, log}) => {
                expect(result).toBe("success");
                expect(log).toContainEqual("1");
                expect(log).toContainEqual("2");
                expect(log).toContainEqual("false");
            }
        }))
        describe("Job if", () => {
            it('regexp', async () => test({
                given: () => {
                    context.event = {
                        additionalProperties: {
                            commit: {
                                commit: "Should not trigger A\n" +
                                    "\n" +
                                    "Change-Id: I3f5dc8b204e75a2331b2e05b27c7e45dd49f1bd4"
                            }
                        }
                    }
                    context.job = {
                        if: "${{ not (internal.event.additionalProperties.commit.commit | contains('/Should not tri..er A/')) }}"
                    }
                    context.steps = [
                        {run: "exit 1"}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("skipped");
                }
            }))
            it('string', async () => test({
                given: () => {
                    context.event = {
                        additionalProperties: {
                            commit: {
                                commit: "Should not trigger B\n" +
                                    "\n" +
                                    "Change-Id: I3f5dc8b204e75a2331b2e05b27c7e45dd49f1bd4"
                            }
                        }
                    }
                    context.job = {
                        if: "${{ not (internal.event.additionalProperties.commit.commit | contains('Should not trigger B')) }}"
                    }
                    context.steps = [
                        {run: "exit 1"}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("skipped");
                }
            }))
            it('Should run', async () => test({
                given: () => {
                    context.event = {
                        additionalProperties: {
                            commit: {
                                commit: "Should trigger C\n" +
                                    "\n" +
                                    "Change-Id: I3f5dc8b204e75a2331b2e05b27c7e45dd49f1bd4"
                            }
                        }
                    }
                    context.job = {
                        if: "${{ (internal.event.additionalProperties.commit.commit | contains('Should trigger C')) }}"
                    }
                    context.steps = [
                        {run: "exit 0"}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                }
            }))
        })
        describe("Scripts", () => {
            it('always()', async () => test({
                given: () => {
                    context.steps = [
                        {name: "Should fail", run: "exit 1"},
                        {name: "Should not run", run: "echo 'Should not be present'"},
                        {name: "Should run", run: "echo 'Should be present'", if: "${{ always() }}"}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("failure");
                    expect(log).toContainEqual("Should be present");
                    expect(log).not.toContainEqual("Should not be present");
                }
            }), timeout);
        })
        describe("Run", () => {
            it('single-line', async () => test({
                given: () => {
                    context.steps = [
                        {run: "echo \"works\""}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    expect(log).toContainEqual("works");
                }
            }), timeout);

            it('multiline', async () => test({
                given: () => {
                    context.steps = [
                        {run: "echo \"works\"\necho \"works 2\""}
                    ]
                },
                then: ({result, log}) => {
                    expect(result).toBe("success");
                    expect(log).toContainEqual("works");
                    expect(log).toContainEqual("works 2");
                }
            }), timeout);
        })
    })

    beforeEach(async () => {
        context = {workflowName: "workflow"}
        try {
            prepareTestPaths();
            context.server = await gitServer().start()
        } catch (e) {
            console.error(`> Setup failed due to ${e}`)
            throw e
        }
    }, timeout);

    afterEach(async () => {
        rmSync(context.repositoryPath, {recursive: true})
        try {
            await context.server?.stop()
            console.log('< Containers stopped')
        } catch (e) {
            console.error(`< Stopping containers failed due to ${e}`)
            throw e
        }
    }, timeout)
})


function prepareTestPaths() {
    context.testRepository = tempDirectory();
    console.log(`> Created test directory: ${context.testRepository}`)
    context.repositoryPath = `${context.testRepository}/repository`;
    mkdirSync(context.repositoryPath, {recursive: true})
    context.secretsPath = `${context.testRepository}/secrets`;
    mkdirSync(context.secretsPath, {recursive: true})
    context.resultPath = `${context.testRepository}/result.json`;
    writeFileSync(context.resultPath, "")
    context.environmentPath = `${context.testRepository}/environment`;
    mkdirSync(context.environmentPath, {recursive: true})
    context.eventPath = `${context.testRepository}/event.yml`;
}

function tempDirectory() {
    return mkdtempSync(join(tmpdir(), randomString()));
}

async function test({given, then}) {
    const run = async (func, params) => typeof func === 'function' ? func?.(params) : await func?.(params)

    await run(given)

    writeEvent();
    console.log(context.steps)
    if (context.steps !== undefined) {
        const workflow = JSON.stringify({
            name: "Tested workflow",
            on: ["push"],
            ...(context.workflow ?? {}),
            jobs: {
                TestedJob: {
                    "runs-on": "docker.home.arpa/venthe/ubuntu-runner:22.10",
                    ...(context.job ?? {}),
                    steps: context.steps
                }
            }
        })
        mkdirSync(`${context.repositoryPath}/.pipeline/workflows`, {recursive: true})
        writeFileSync(`${context.repositoryPath}/.pipeline/workflows/${context.workflowName}.yaml`, workflow)
    } else {
        await copyRepository()
    }
    await uploadRepository()
    let newVar = await executeRunner();
    context.output = {
        ...JSON.parse(readFileSync(context.resultPath, {encoding: "utf-8"})),
        log: newVar.raw.split("\r\n")
    }
    await run(then, context.output)
}

async function copyRepository() {
    const source = `${process.cwd()}/resources/${context.directory}`;

    cpSync(source, context.repositoryPath, {recursive: true})
}

function repository(projectName = "repository.git") {
    return {
        url: `http://host.docker.internal:${context.server.getMappedPort(80)}`,
        name: projectName
    };
}

async function uploadRepository() {
    return command(`
            git init
            git add --all
            git commit -m "Initial commit"
            git push ${repository().url}/${repository().name} --force
        `, {cwd: context.repositoryPath})
}

function command(cmd, options) {
    return new Promise((resolve, reject) => {
        const _spawn = spawn(cmd, {shell: true, ...(options ?? {})});

        const stdout = [];
        const stderr = [];

        _spawn.stdout.on('data', data => {
            const strData = data.toString();
            console.log(strData);
            stdout.push(strData);
        });

        _spawn.stderr.on('data', data => {
            const strData = data.toString();
            console.debug(strData);
            stderr.push(strData);
        });

        _spawn.on('close', code => {
            resolve({stdout, stderr, code})
        });
    })
}

function executeRunner() {
    return dockerCommand(
        [
            'run',
            '--rm',
            '--tty',
            `--volume "${process.env.HOME}/.kube/config:/root/.kube_test/config:ro"`,
            `--volume "${process.env.HOME}/.ssh/:/root/.ssh_test:ro"`,
            `--volume "${process.cwd()}/../dist/index.js:/runner/index.js"`,
            `--volume "${process.cwd()}/../dist/sourcemap-register.js:/runner/sourcemap-register.js"`,
            `--volume "${process.cwd()}/../dist/index.js.map:/runner/index.js.map"`,
            `--volume '${context.eventPath}:/runner/metadata/event.yaml:ro'`,
            `--volume '${context.environmentPath}:/runner/metadata/env:ro'`,
            `--volume '${context.secretsPath}:/runner/metadata/secrets:ro'`,
            `--volume '${context.resultPath}:/runner/result.json'`,
            `--volume "${process.cwd()}/test.sh:/test.sh"`,
            '--volume "/etc/ssl/certs/ca-certificates.crt:/etc/ssl/certs/ca-certificates.crt:ro"',
            '--volume "/usr/local/share/ca-certificates/k8s/ca.crt:/certs/ca.crt:ro"',
            '--privileged',
            '--env PIPELINE_JOB_NAME="TestedJob"',
            '--env PIPELINE_BUILD_ID="1"',
            '--env PIPELINE_DEBUG="0"',
            `--env PIPELINE_WORKFLOW="${context.workflowName}.yaml"`,
            '--env PIPELINE_NEXUS_URL="https://nexus.home.arpa/repository/raw-hosted"',
            '--env PIPELINE_DOCKER_URL="https://docker.home.arpa"',
            '--env PIPELINE_GERRIT_URL="ssh://admin@ssh.gerrit.home.arpa:29418"',
            '--env GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no"',
            'docker.home.arpa/venthe/ubuntu-runner:22.10',
            '/test.sh'
        ].join(" "));
}

function writeEvent() {
    let obj = {
        type: "PatchsetCreated",
        ...(context.event ?? {}),
        metadata: {
            projectName: repository().name,
            branchName: "main",
            revision: "main",
            url: repository().url,
            ...(context.event?.metadata ?? {})
        }
    };
    writeFileSync(context.eventPath, yaml.dump(obj), "UTF-8")
}

function gitServer() {
    return new GenericContainer('docker.home.arpa/venthe/git-server')
        .withExposedPorts(80)
        .withBindMounts([
            {source: `${process.env.HOME}/.gitconfig`, target: `/root/.gitconfig`, mode: "ro"}
        ]);
}

function withDockerSecret() {
    writeFileSync(`${context.secretsPath}/docker.json`, JSON.stringify({
            "password": "secret",
            "username": "admin"
        }
    ))
}

function withNexusSecret() {
    writeFileSync(`${context.secretsPath}/nexus.json`, JSON.stringify({
        "password": "secret",
        "username": "admin"
    }))
}
