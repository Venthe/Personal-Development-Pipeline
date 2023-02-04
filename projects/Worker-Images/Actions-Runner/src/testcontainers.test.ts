import {GenericContainer} from "testcontainers";

export const token = "vault_development_token";
export const vaultAddress = port => "http://localhost:" + port;
export const vaultPort = 8200

export const vaultContainer: GenericContainer = new GenericContainer("docker.io/library/vault:1.12.2")
    .withExposedPorts(vaultPort)
    .withEnvironment({
        VAULT_DEV_ROOT_TOKEN_ID: token,
        VAULT_DEV_LISTEN_ADDRESS: `0.0.0.0:${vaultPort}`,
        VAULT_ADDR: vaultAddress(vaultPort),
        APPLICATION_NAME: "example"
    })
    .withAddedCapabilities("IPC_LOCK")
    .withCommand(["-dev"])
    .withEntrypoint(["/mnt/init/init.sh"])
    .withBindMounts([
        {source: `${process.cwd()}/test/vault/secrets/example.json`, target: "/mnt/init/secrets/example.json",  mode: "ro"},
        {source: `${process.cwd()}/test/vault/init.sh`, target: "/mnt/init/init.sh", mode: "ro"}
    ])
