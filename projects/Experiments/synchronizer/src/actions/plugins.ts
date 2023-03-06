// export const registeredPluginFactory: {[kind: string]: PluginDefinition<any>} = {}

export type StateDefinition<U extends String, T extends object> = {
    apiVersion: "synchronizer/v1alpha"
    kind: U
    spec: T
}

export type PluginDefinition<U extends String, T extends object> = {
    kind: string
    apiVersion: "synchronizer/v1alpha"
    spec: T
}

export interface Plugin<T> {
    synchronize: (state: T) => void
}

//
// export function RegisterPlugin(pluginDefinition: PluginDefinition<any>) {
//     console.log("Registering plugin")
//     registeredPluginFactory[pluginDefinition.kind] = pluginDefinition
// }
//
// export const getPlugin = <U, W>(state: StateDefinition<U>): (configuration) => Plugin<any> =>
//     Object.keys(registeredPluginFactory)
//         .map(key => registeredPluginFactory[key])
//         .filter(plugin => plugin.stateKind === state.kind)[0];
//
