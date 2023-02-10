import {download, upload} from "./artifacts";
import {ContextSnapshot} from "@pipeline/types";

function sampleContext(): ContextSnapshot & any {
    return {
        secrets: {
            NEXUS_USERNAME: "admin",
            NEXUS_PASSWORD: "secret"
        },
        internal: {
            nexusUrl: "https://nexus.home.arpa/repository/raw-hosted",
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
}

describe('artifacts', () => {
    it('should upload', async () => {
        const context = sampleContext();

        await upload({sourcePath: "./.npmignore", context})
    });

    it('should download', async () => {
        const context = sampleContext();

        await download({sourcePath: "./.npmignore", targetPath: "./.npmignore", context})
    });
});
