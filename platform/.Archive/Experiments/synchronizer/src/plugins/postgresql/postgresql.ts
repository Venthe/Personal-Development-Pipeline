import {connect, execute, query} from "./common";
import {
    ExistingRole,
    PostgresPluginSpecificDefinition,
    PostgreSQLDatabaseState,
    PostgreSQLPluginDefinition,
    PostgreSQLStateDefinition
} from "./types";
import {loadYamlFile} from "../../filesystem";
import {synchronizeRoles} from "./synchronizeRoles";
import {synchronizeSchemas} from "./synchronizeSchemas";
import {sets} from "../../actions/utilities";

type ExpectedState = {
    name: string,
    state: PostgreSQLDatabaseState,
    database: PostgresPluginSpecificDefinition
};

export async function synchronizePostgreSQL(values) {
    const config = loadYamlFile<PostgreSQLPluginDefinition>(values["config"])
    const state = loadYamlFile<PostgreSQLStateDefinition>(values["state"]);

    const dbConnection = await connect(config.spec.connection);

    console.error("Synchronizing database")
    const databases = await query(dbConnection, `
        SELECT datname                                                   as "databaseName",
               pg_catalog.pg_get_userbyid(pg_catalog.pg_database.datdba) as "owner"
        FROM pg_catalog.pg_database
        where datistemplate = false
    `)

    const existingDatabaseNames = databases.map(a => a.databaseName);
    const expectedDatabaseNames = Object.keys(state.spec.databases)

    const databaseChangeSet = sets(existingDatabaseNames, expectedDatabaseNames, {
        createdStateMapper: dbKey => ({
            name: dbKey,
            state: state.spec.databases[dbKey],
            database: config.spec
        }),
        expectedStateMapper: dbKey => ({
            name: dbKey,
            state: state.spec.databases[dbKey],
            database: config.spec
        })
    });
    for (const db of databaseChangeSet.toCreate) {
        await execute(dbConnection, [
            "CREATE DATABASE",
            (db as any as { name }).name
        ].join(" "))
    }

    const existingRoles: ExistingRole[] = (await query(dbConnection, `
        select rolname     as "roleName",
               rolcanlogin as "canLogin",
               oid
        from pg_roles
    `))
        .filter(a => !a.roleName.startsWith("pg_"))
    await synchronizeRoles(existingRoles, {database: config.spec, roles: state.spec.roles ?? {}}, dbConnection);

    await dbConnection.close();

    // TODO: Disable database
    // TODO: Change database owner
    for (const it of [
        ...databaseChangeSet.toCreate as ExpectedState[],
        ...databaseChangeSet.toSynchronize
            .map(a =>
                (a as {
                    expectedState: ExpectedState
                }).expectedState)
    ]
        ) {
        //console.debug(it, [...databaseChangeSet.toSynchronize, ...databaseChangeSet.toCreate])
        const connection = await connect(config.spec.connection, it.name);
        const existingSchemas = (await query(connection, `
            SELECT schema_name  as "schemaName",
                   schema_owner as "schemaOwner"
            FROM information_schema.schemata
            WHERE schema_name != 'information_schema'
              and schema_name not like 'pg_%'
        `));
        const existingGrants = ((await query(connection, `
            SELECT n.nspname AS "schemaName",
                   r.rolname AS "roleName",
                   p.perm    AS privilege
            FROM pg_catalog.pg_namespace AS n
                     CROSS JOIN pg_catalog.pg_roles AS r
                     CROSS JOIN (VALUES ('USAGE'), ('CREATE')) AS p(perm)
            WHERE n.nspname != 'information_schema'
              AND n.nspname NOT LIKE 'pg_%'
              AND r.rolname NOT LIKE 'pg_%'
              AND r.rolinherit = false
            ORDER BY 1, 2
        `)));
        await synchronizeSchemas(it, existingSchemas, existingGrants, connection);

        await connection.close();
    }
}
