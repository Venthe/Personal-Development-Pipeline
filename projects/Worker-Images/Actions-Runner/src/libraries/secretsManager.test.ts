/*
import {SecretsManager} from './secretsManager'
import {token, vaultAddress, vaultContainer, vaultPort} from "../testcontainers.test";
import {StartedTestContainer} from "testcontainers/dist/test-container";

type ExampleData = { key: number, nested: { key: number } };

describe("Secrets manager", () => {

    let vault: StartedTestContainer;
    let secretManager: SecretsManager;

    beforeAll(async () => {
        vault = await vaultContainer.start()
        const port = vault.getMappedPort(vaultPort);

        await new Promise(r => setTimeout(r, 5000));

        secretManager = SecretsManager.create({
            url: `${vaultAddress(port)}/v1`,
            token
        })
    }, 25000);

    afterAll(async () => {
        await vault.stop()
    })

    test('Can read a secret', async () => {
        let data = await secretManager.get<ExampleData>("example");

        expect(data.key)
            .toBe(1);
        expect(data.nested.key)
            .toBe(3);
    });
})

describe("Secrets manager without test containers", () => {

    let secretManager: SecretsManager;

    beforeAll(async () => {
        await new Promise(r => setTimeout(r, 5000));

        secretManager = SecretsManager.create({
            url: `${vaultAddress(8200)}/v1`,
            secretName: "pipeline",
            token
        })
    }, 25000);

    test('Can read a secret', async () => {
        let data = await secretManager.retrieve();

        expect(data.nexus.password)
            .toBe("secret");
        expect(data.docker.username)
            .toBe("docker");
    });
})
*/
