import {ActionInputs} from "./actionInputs";

export interface BaseActionDefinition {
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
        color?: "white" | "yellow" | "blue" | "green" | "orange" | "red" | "purple" | "gray-dark"
    }
    /**
     * Input parameters allow you to specify data that the action expects to use during runtime. GitHub stores input
     * parameters as environment variables. Input ids with uppercase letters are converted to lowercase during runtime.
     * We recommended using lowercase input ids.
     **/
    inputs?: ActionInputs
    runs: {
        using?: string
    }
}
