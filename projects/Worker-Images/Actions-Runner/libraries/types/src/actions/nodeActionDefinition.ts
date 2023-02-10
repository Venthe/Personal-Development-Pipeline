import {BaseActionDefinition} from "./baseActionDefinition";

export interface NodeActionDefinition extends BaseActionDefinition {
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