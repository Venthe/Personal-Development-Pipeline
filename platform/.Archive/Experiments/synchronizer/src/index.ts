import {parseArgs} from 'node:util';
import {synchronizePostgreSQL} from "./plugins/postgresql/postgresql";

(async () => {
    const args = process.argv;
    const options: any = {
        state: {
            type: 'string',
            short: 's'
        },
        config: {
            type: 'string'
        }
    };
    const {
        values,
        positionals
    } = parseArgs({args, options, allowPositionals: true});
    await synchronizePostgreSQL(values);
})()

