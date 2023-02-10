import {StepDefinition} from "./stepDefinition";

export interface ActionStepDefinition<T extends object = {}> extends StepDefinition {
    uses: string
    with?: T
}