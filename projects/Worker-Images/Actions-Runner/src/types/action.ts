import {Dictionary} from "./common";
import {ActionStep, ShellStep} from "./steps";

export interface DockerOutput {
    description: string
}

export interface CompositeOutput {
    description: string
    /**
     * The value that the output parameter will be mapped to. You can set this to a string or an expression with context.
     * For example, you can use the steps context to set the value of an output to the output value of a step.
     *
     * ${{ steps.random-number-generator.outputs.random-id }}
     **/
    value: string
}

export interface DockerAction {
    runs: {
        using: "docker"
        preEntrypoint?: string
        image: string
        env?: Dictionary<string>
        workingDirectory?: string
        with?: Dictionary<string>
    }
    outputs?: Dictionary<DockerOutput>
}

export interface CompositeAction {
    runs: {
        using: "composite"
        steps: (ShellStep | ActionStep)[]
    }
    outputs?: Dictionary<CompositeOutput>
}

export interface NodeAction {
    /**
     * Specifies whether this is a JavaScript action, a composite action, or a Docker container action and how the action is executed.
     **/
    runs: {
        using: "node"
        /*
        * Optional Allows you to run a script at the start of a job, before the main: action begins. For example,
        * you can use pre: to run a prerequisite setup script. The runtime specified with the using syntax will execute
        * this file. The pre: action always runs by default but you can override this using runs.pre-if.
        */
        pre?: string
        preIf?: string
        /*
        * The file that contains your action code. The runtime specified in using executes this file.
        */
        main: string;
        post?: string
        postIf?: string
    }
}

export interface Input {
    /**
     * A string description of the input parameter.
     */
    required?: boolean
    /**
     * A boolean to indicate whether the action requires the input parameter. Set to true when the
     * parameter is required.
     */
    default?: any
    /**
     * A string identifier to associate with the input. The value of <input_id> is a map of the input's metadata.
     * The <input_id> must be a unique identifier within the inputs object. The <input_id> must start with a letter
     * or _ and contain only alphanumeric characters, -, or _.
     */
    description: String
    /**
     * If the input parameter is used, this string is logged as a warning message. You can use this warning
     * to notify users that the input is deprecated and mention any alternatives.
     */
    deprecationMessage?: String
}

interface BaseAction {
    /**
     * The name of your action. GitHub displays the name in the Actions tab to help visually identify actions in each job.
     */
    name: string
    /** The name of the action's author. **/
    author?: string
    /** A short description of the action. **/
    description?: string
    branding?: {
        /** The name of the v4.28.0 Feather icon to use. **/
        icon: string
        /** The background color of the badge. **/
        color: "white" | "yellow" | "blue" | "green" | "orange" | "red" | "purple" | "gray-dark"
    }
    /**
     * Input parameters allow you to specify data that the action expects to use during runtime. GitHub stores input
     * parameters as environment variables. Input ids with uppercase letters are converted to lowercase during runtime.
     * We recommended using lowercase input ids.
     **/
    inputs?: { [inputId: string]: Input }
}

export type Action = BaseAction & (NodeAction | CompositeAction | DockerAction)
