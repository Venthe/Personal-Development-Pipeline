/* eslint-disable */
export default {
    displayName: 'Actions runner',
    testEnvironment: 'node',
    transform: {
        "^.+\\.[jt]sx?$": ["ts-jest", { tsconfig: `<rootDir>/tsconfig.spec.json` }],
    },
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
    coverageDirectory: 'src',
    setupFilesAfterEnv: ["./test/config.ts"]
};
