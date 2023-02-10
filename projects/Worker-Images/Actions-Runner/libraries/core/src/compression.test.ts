import {shell} from "@pipeline/process";
import {untar, unzip} from "./compression";

const testCatalogName = ".test";

async function grabMD5(fileName: string) {
    const initialFileMd5: string[] = [];
    await shell(`md5sum ${testCatalogName}/${fileName}`, {callbacks: {stdout: (c) => initialFileMd5.push(c.chunk)}})
    return initialFileMd5[0];
}

async function prepareTestFile(testFileName: string) {
    await shell(`touch ${testCatalogName}/${testFileName}`)
    await shell(`printf 'sample data' > ${testCatalogName}/${testFileName}`)
}

describe('compression', () => {
    beforeAll(async () => {
        await shell("mkdir -p " + testCatalogName)
    })

    afterAll(async () => {
        await shell("rm -rf .test")
    })

    it('untar works', async () => {
        // given
        const testFileName = "tarfile";
        const archiveName = `${testFileName}.tar.gz`;

        await prepareTestFile(testFileName);
        await shell(`cd ${testCatalogName} && tar -czvf ${archiveName} ${testFileName}`)
        const initialFileMd5 = await grabMD5(testFileName);
        await shell(`rm -rf ${testCatalogName}/${testFileName}`)

        // when
        await untar(`${testCatalogName}/${archiveName}`, testCatalogName)
        const unpackedMD5 = await grabMD5(testFileName);

        // then
        expect(unpackedMD5).toEqual(initialFileMd5);
    });

    it('unzip works', async () => {
        // given
        const testFileName = "zipfile";
        const archiveName = `${testFileName}.zip`;

        await prepareTestFile(testFileName);
        await shell(`cd ${testCatalogName} && zip ${archiveName} ${testFileName}`)
        const initialFileMd5 = await grabMD5(testFileName);
        await shell(`rm -rf ${testCatalogName}/${testFileName}`)

        // when
        await unzip(`${testCatalogName}/${archiveName}`, testCatalogName)
        const unpackedMD5 = await grabMD5(testFileName);

        // then
        expect(unpackedMD5).toEqual(initialFileMd5);
    });
});
