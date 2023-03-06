import {PluginDefinition, StateDefinition} from "../../actions/plugins";

export type PostgresConnection = {
    url: string
    username: string
    password: string
};

export type PostgresPluginSpecificDefinition = {
    connection: PostgresConnection
    resultEncryptionKey: string
};

export type PostgreSQLPluginDefinition = PluginDefinition<"PostgreSQLConfiguration", PostgresPluginSpecificDefinition>

export type RoleState = {
    options?: {
        option: string
        arguments?: string[]
    }[]
};

export type SchemaState = {
    owner?: string
    authorizedFor?: {
        [role: string]: "ALL_PRIVILEGES" | string[]
    }
};

export type SchemasState = {
    [schemaName: string]: SchemaState
};

export type RolesStates = {
    [roleName: string]: RoleState
};

export type PostgreSQLDatabaseState = {
    owner?: string
    schemas?: SchemasState
};

export type PostgreSQLStateDefinition = StateDefinition<"PostgreSQLState", {
    databases: {
        [databaseName: string]: PostgreSQLDatabaseState
    }
    roles?: RolesStates
}>

export interface ExistingRole {
    roleName: string
    canLogin: boolean
    oid: number
}
