import {ActionStepDefinition, ShellStepDefinition} from "../steps";
import {BaseActionDefinition} from "./baseActionDefinition";

export interface CompositeOutputDefinition {
    description?: string
    /**
     * The value that the output parameter will be mapped to. You can set this to a string or an expression with context.
     * For example, you can use the steps context to set the value of an output to the output value of a step.
     *
     * ${{ steps.random-number-generator.outputs.random-id }}
     **/
    value: string
}

export interface CompositeActionDefinition extends BaseActionDefinition {
    runs: {
        using: "composite"
        steps: (ShellStepDefinition | ActionStepDefinition)[]
    }
    outputs?: {
        [key: string]: CompositeOutputDefinition
    }
}