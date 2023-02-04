import {ContextEnvironmentVariables} from "../configuration";
import fs from "fs";

export class SecretsManager {
    private constructor(private readonly environmentVariables: ContextEnvironmentVariables) {
    }

    public static create(environmentVariables: ContextEnvironmentVariables) {
        return new SecretsManager(environmentVariables);
    }

    public retrieve(): object {
        let result = {}
        const files = fs.readdirSync(this.environmentVariables.VPIPELINE_RUNNER_SECRETS_DIRECTORY)
        files.forEach(file => {
            let key = file.replace(".json", "")
            result[key] = JSON.parse(fs.readFileSync(`${this.environmentVariables.VPIPELINE_RUNNER_SECRETS_DIRECTORY}/${file}`, 'utf8'))
        });
        return result;
    }
}
