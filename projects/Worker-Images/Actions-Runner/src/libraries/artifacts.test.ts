import {upload} from "./artifacts";
import {VentheActionsContext} from "../types";

describe('artifacts', () => {
    it('should work', async () => {
        const context: any & VentheActionsContext = {
            secrets: {
                nexus: {
                    username: "admin",
                    password: "secret"
                }
            },
            internal: {
                nexus: {
                    url: "https://nexus.home.arpa/repository/raw-hosted"
                },
                event: {
                    change: {
                        project: "test",
                        number: "1"
                    },
                    patchSet: {
                        number: "1"
                    }
                }
            }
        };
        await upload({sourcePath: "./.npmignore", targetPath:"", context})
    });
});
