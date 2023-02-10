import {ActionStepDefinition} from "./actionStepDefinition";

export interface DockerStepDefinition extends ActionStepDefinition<{
    [key: string]: string | undefined
    /** in docker **/
    entrypoint?: string
}> {
}