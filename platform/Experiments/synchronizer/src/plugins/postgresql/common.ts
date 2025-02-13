import {Connection} from "postgresql-client";

export async function query(connection, sql, options?) {
    // console.debug(sql)
    const result = await connection.query(
        sql,
        options);
    console.error(`${result.command}: ${result.executeTime}ms`)
    return result.rows.map(r => {
        const returnObject = {}
        result.fields.sort((a, b) => a.columnId > b.columnId).map(r => r.fieldName).forEach((col, idx) => {
            returnObject[col] = r[idx]
        })
        return returnObject
    });
}

export async function execute(connection: Connection, sql: string, options?) {
    console.debug(`*\tsql`)
    const result = await connection.execute(sql, options);
    console.error(result.results.map(a => `${a.command}: ${a.executeTime}ms`))
}

export async function connect(connectionConfiguration: { url: string; username: string; password: string }, database?) {
    const connection = new Connection(`postgres://${connectionConfiguration.username}:${connectionConfiguration.password}@${connectionConfiguration.url}/${database ?? ""}`);
    await connection.connect();
    return connection;
}
