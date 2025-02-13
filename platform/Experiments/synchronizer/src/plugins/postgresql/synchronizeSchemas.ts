import {Connection} from "postgresql-client";
import {execute, query} from "./common";
import {groupBy, sets} from "../../actions/utilities";

export async function synchronizeSchemas(it, existingSchemas, existingGrants, connection: Connection) {
    console.error("Synchronizing schemas for database " + it.name)

    const currentSchemaNames = existingSchemas.map(a => a.schemaName);
    // console.debug(it)
    const expectedSchemaNames = Object.keys(it.state.schemas ?? {});

    // console.debug(it)
    const _sets = sets(
        currentSchemaNames,
        expectedSchemaNames,
        {
            expectedStateMapper: sn => ({
                name: sn,
                state: (it.state.schemas ?? {})[sn]
            }),
            currentStateMapper: sn => existingSchemas
                .filter(es => es.schemaName === sn)
                .map(es => ({
                    name: es.schemaName,
                    state: {owner: es.schemaOwner}
                }))[0]
        }
    )
    const grantsForSchemas = groupBy(existingGrants, "schemaName");

    for (const schema of _sets.toCreate) {
        console.error(`Creating schema ${schema}`)
        await execute(connection, `CREATE SCHEMA IF NOT EXISTS ${schema}`)
    }
    // FIXME
    // for (const schema of _sets.toRemove) {
    //     console.error(`Revoking access to ${schema}`)
    //     await execute(connection, `REVOKE ALL ON TABLE ALL TABLES IN SCHEMA ${schema}`)
    // }
    for (const {currentState, expectedState} of _sets.toSynchronize as any) {
        console.error(`Synchronizing schema ${currentState.name}`)

        if (currentState.name !== expectedState.name) {
            console.error(`Renaming schema from ${currentState.name} to ${expectedState.name}`)
            await execute(connection, `ALTER SCHEMA ${currentState.name} RENAME TO ${expectedState.name}`)
        }

        if (currentState.owner !== expectedState.owner) {
            if (!expectedState.owner) {
                console.error(`Removing owner from schema ${expectedState.name}`)
                await execute(connection, `ALTER SCHEMA ${expectedState.name} OWNER TO ${it.database.connection.username}`)
            } else {
                console.error(`Changing owner in schema ${expectedState.name} from ${currentState.owner} to ${expectedState.owner}`)
                await execute(connection, `ALTER SCHEMA ${expectedState.name} OWNER TO ${expectedState.owner}`)
            }
        }

        const grantsForSchema = groupBy(grantsForSchemas[expectedState.name]?.map(a => {
            const result = {...a}
            delete result["schemaName"];
            return result;
        }) ?? [], "roleName")
        for (const expected of Object.keys(expectedState.state?.authorizedFor ?? [])
            .map(key => ({
                schemaName: expectedState.name,
                roleName: key,
                grants: expectedState.state.authorizedFor[key]
            }))) {
            const currentGrants = (grantsForSchema[expected.roleName]?.map(a => {
                const result = {...a}
                delete result["roleName"];
                return result;
            }) ?? [])
            const expectedGrants = typeof expected.grants === 'string' ? [expected.grants] : expected.grants

            const data = sets(currentGrants, expectedGrants);

            // console.debug(currentGrants, expectedGrants)

            if (data.toCreate.length > 0) {
                const grantPrivileges = [
                    `GRANT`,
                    data.toCreate.join(", "),
                    `ON SCHEMA ${expected.schemaName}`,
                    `TO ${expected.roleName}`
                ].join(" ")
                // console.debug(grantPrivileges)
                await execute(connection, grantPrivileges)
            }

            if (data.toRemove.length > 0) {
                const revokePrivileges = [
                    `REVOKE`,
                    data.toRemove.join(", "),
                    `ON SCHEMA ${expected.schemaName}`,
                    `FROM ${expected.roleName}`
                ].join(" ")
                // console.debug(revokePrivileges)
                await execute(connection, revokePrivileges)
            }
        }
    }
}
