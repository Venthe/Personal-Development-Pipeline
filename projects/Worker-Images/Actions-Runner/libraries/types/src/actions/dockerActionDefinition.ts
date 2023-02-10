import {BaseActionDefinition} from "./baseActionDefinition";

interface DockerOutputDefinition {
    description: string
}

export interface DockerActionDefinition extends BaseActionDefinition {
    runs: {
        using: "docker"
        preEntrypoint?: string
        image: string
        env?: {
            [key: string]: string
        }
        workingDirectory?: string
        with?: {
            [key: string]: string
        }
    }
    outputs?: {
        [key: string]: DockerOutputDefinition
    }
}