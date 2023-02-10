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

export type ActionInputs = { [inputId: string]: Input };