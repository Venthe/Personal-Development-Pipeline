import {Expression} from "../commonTypes";

export interface StepDefinition {
    /**
     * You can use the if conditional to prevent a step from running unless a condition is met. You can use
     * any supported context and expression to create a conditional. For more information on which contexts
     * are supported in this key, see "Context availability."
     *
     * When you use expressions in an if conditional, you may omit the expression syntax (${{ }}) because
     * GitHub automatically evaluates the if conditional as an expression. For more information, see
     * "Expressions."
     */
    if?: Expression
    /**
     * A name for your step to display on GitHub.
     */
    name?: string
    /**
     * A unique identifier for the step. You can use the id to reference the step in contexts. For more
     * information, see "Contexts."
     */
    id?: string
}

