import {Connection} from "postgresql-client";
import {ExistingRole, PostgresPluginSpecificDefinition, RolesStates, RoleState} from "./types";
import {execute, query} from "./common";

export async function createRoles(roles, rolesToCreate: string[], connection: Connection) {
    for (const {roleName, role} of rolesToCreate.map(rn => ({roleName: rn, role: roles[rn]}))) {
        const sql = [
            `CREATE ROLE ${roleName}`,
            ...(
                (role.options && role.options.length > 0)
                    ? [
                        "WITH",
                        ...role.options.flatMap(opt => [opt.option.trim(), (opt?.arguments ?? []).map(arg => `${arg}`).join(", ").trim()])
                    ]
                    : []
            )
        ].join(" ").trim()

        console.error(`Creating ${roleName}`)
        await execute(connection, sql)
    }
}

async function disableRoles(rolesToDisable: string[], connection: Connection) {
    for (const role of rolesToDisable) {
        const sql = `ALTER USER ${role}
                   WITH NOLOGIN`;
        console.error(`Disabling ${role}: ${sql}`)
        await execute(connection, sql);
    }
}

const getArguments = (role: RoleState, option) =>
    role?.options
        ?.filter(opt => opt.option.toLowerCase() === option.toLowerCase())
        ?.[0]
        ?.arguments

const forOption = (handledRoles, roles) => (option: string) => handledRoles
    .map(rn => ({
        roleName: rn,
        role: roles[rn]
    }))
    .map(r => ({
        arguments: getArguments(r.role, option),
        ...r
    }))
    .filter(r => r.arguments);

async function getCurrentRoleState(connection: Connection, role: any) {
    return (await query(connection, `
        SELECT r.rolname                     as "roleName",
               r.rolsuper,
               r.rolinherit,
               r.rolcreaterole,
               r.rolcreatedb,
               r.rolcanlogin,
               r.rolconnlimit,
               r.rolvaliduntil,
               ARRAY(SELECT b.rolname
                     FROM pg_catalog.pg_auth_members m
                              JOIN pg_catalog.pg_roles b ON (m.roleid = b.oid)
                     WHERE m.member = r.oid) as "memberOf",
               r.rolreplication
        FROM pg_catalog.pg_roles r
        WHERE rolname = '${role.roleName}'
        ORDER BY 1;
    `))[0];
}

async function addRoles(rolesToAdd, role: any, connection: Connection) {
    for (const command of rolesToAdd.map(rta => `GRANT ${rta} TO ${role.roleName}`)) {
        console.error("Adding role: " + command)
        await execute(connection, command)
    }
}

async function removeRoles(rolesToRemove, role, connection: Connection) {
    for (const command of rolesToRemove.map(rta => `REVOKE ${rta} FROM ${role.roleName}`)) {
        console.error("Removing role: " + command)
        await execute(connection, command)
    }
}

async function enableRole(role, connection: Connection) {
    const sql = `ALTER USER ${role} WITH LOGIN'`;
    console.error(`Enabling ${role.roleName}: ${sql}`)
    await execute(connection, sql);
}

async function synchronizeLogin(_forOption, connection: Connection) {
    for (const role of _forOption("login")) {
        await enableRole(role, connection);
    }
}

async function _synchronizeRoles(_forOption, connection: Connection) {
    for (const role of _forOption("in role")) {
        const roleState = await getCurrentRoleState(connection, role)
        const currentRoles = roleState.memberOf
        const expectedRoles = role.arguments ?? []

        const rolesToAdd = expectedRoles.filter(er => !currentRoles.map(a => a.toLowerCase()).includes(er.toLowerCase()))
        await addRoles(rolesToAdd, role, connection);

        const rolesToRemove = currentRoles.filter(er => !expectedRoles.map(a => a.toLowerCase()).includes(er.toLowerCase()))
        await removeRoles(rolesToRemove, role, connection);
    }
}

function getRolesToBeDisabled(existingRoles: string[], expectedRoles: string[], roles) {
    return [
        ...existingRoles.filter(a => !expectedRoles.includes(a)),
        ...expectedRoles.filter(er => roles[er]?.options.find(el => el.option.toLowerCase() === "nologin"))
    ];
}

export async function synchronizeRoles(existingRoles, {
                                           database,
                                           roles
                                       }: { database: PostgresPluginSpecificDefinition, roles: RolesStates }, connection: Connection) {
    console.error("Synchronizing roles")
    const expectedRoles: string[] = [database.connection.username, ...Object.keys(roles)]
    const existingRoleNames: string[] = existingRoles.map(a => a.roleName)

    const rolesToCreate: string[] = expectedRoles.filter(a => !existingRoleNames.includes(a))
    await createRoles(roles, rolesToCreate, connection);

    const rolesToDisable: string[] = getRolesToBeDisabled(existingRoleNames, expectedRoles, roles)
    // console.debug(rolesToDisable, existingRoleNames, expectedRoles, roles)
    await disableRoles(rolesToDisable, connection);

    const handledRoles = [...new Set([...existingRoles, ...expectedRoles])]
    const _forOption = forOption(handledRoles, roles)
    await synchronizeLogin(_forOption, connection);
    await _synchronizeRoles(_forOption, connection);
}
