import {dockerLogin} from "./docker";

import {steps, VentheActionsContext} from "../types";

function step(param: { url?: string; username?: string; password?: string } = {}) {
    return {with: param} as steps.ActionStep<{ url?: string, username?: string, password?: string }>;
}

describe('docker', () => {
    it('login', async () => {
        await dockerLogin(
            step({
                password: "DOCKER_CUSTOM_SECRET",
                username: "admin",
                url: "localhost:5000"
            }),
            {
                secrets: {
                    DOCKER_CUSTOM_SECRET: "secret"
                }
            } as any as VentheActionsContext
        )
    })
    it('login2', async () => {
        await dockerLogin(
            step(),
            {
                internal: {
                    docker: {
                        url: "localhost:5000"
                    }
                },
                secrets: {
                    DOCKER_PASSWORD: "secret",
                    DOCKER_USERNAME: "admin"
                }
            } as any as VentheActionsContext
        )
    })
})
