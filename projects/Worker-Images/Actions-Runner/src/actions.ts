import {ActionStep} from "./types/steps";
import {Context} from "./configuration";
import {checkout} from "./actions/checkout";
import {VentheActionsContext} from "./types";
import {setupJava} from "./actions/java";
import {setupGradle} from "./actions/gradle";
import {dockerLogin, setupDocker} from "./actions/docker";
import {setupKubernetes} from "./actions/kubernetes";
import {uploadArtifact} from "./actions/upload-artifact";

export class Actions {
    constructor(readonly step: ActionStep) {
    }

    async run(context: Context) {
        const action = this.getAction(this.step.uses)

        await action(this.step, await context.getCtx())
    }

    private getAction(uses: string): (step: ActionStep<any>, context: VentheActionsContext) => Promise<any> {
        switch (uses) {
            case "actions/checkout@v1":
                return checkout
            case "actions/setup-java@v1":
                return setupJava
            case "actions/setup-gradle@v1":
                return setupGradle
            case "actions/setup-docker@v1":
                return setupDocker
            case "actions/docker-login@v1":
                return dockerLogin
            case "actions/setup-kubernetes@v1":
                return setupKubernetes
            case "actions/upload-artifact@v1":
                return uploadArtifact
            default:
                throw Error("No action found for step definition")
        }
    }
}
