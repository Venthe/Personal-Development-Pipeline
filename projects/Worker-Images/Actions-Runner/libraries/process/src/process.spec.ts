import {password, shell, StreamType} from "./process";

describe('process', () => {
    it('should work', async () => {
        const resultsWithR: string[] = [];
        const result = await shell("echo a; echo b; echo ar", {
            callbacks: {
                stdout: ({chunk, date, streamType}) => {
                    const c = chunk.split("\n")
                    c.filter(cc => cc.includes("r")).forEach(cc => resultsWithR.push(cc))
                }
            }
        })
        expect(resultsWithR).toStrictEqual(["ar"]);
        expect(result.code).toBe(0);
    });

    it('should not work', async () => {
        try {
            await shell("exit 1")
        } catch (e) {
            expect(e).toEqual({"code": 1, output: []})
        }
    });

    it('should mask patterns', async () => {
        let result = await shell("echo '--password pass'", {
            output: true,
            mask: [password("--password")]
        });

        expect(result.output?.filter(a => a.stream === StreamType.STDOUT).map(a => a.line)).toEqual(["--password *****"])
    });

    it('should mask patterns', async () => {
        let result = await shell("echo '--password pass2 --password2 p2'", {
            output: true,
            mask: [password("--password"), password("--password2")]
        });

        expect(result.output?.filter(a => a.stream === StreamType.STDOUT).map(a => a.line)).toEqual(["--password ***** --password2 *****"])
    });

    it('should mask patterns with :', async () => {
        let result = await shell("echo '--password pa:ss'", {
            output: true,
            mask: [password("--password")]
        });

        expect(result.output?.filter(a => a.stream === StreamType.STDOUT).map(a => a.line)).toEqual(["--password *****"])
    });
});
