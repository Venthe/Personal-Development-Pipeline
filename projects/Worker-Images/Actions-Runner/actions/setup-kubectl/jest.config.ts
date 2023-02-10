/* eslint-disable */
export default {
    displayName: 'Process library',
    testEnvironment: 'node',
    transform: {
        "^.+\\.[jt]sx?$": ["ts-jest", { tsconfig: `<rootDir>/tsconfig.spec.json` }],
    },
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
    coverageDirectory: 'coverage/libraries/core',
    setupFilesAfterEnv: ["./test/config.ts"]
};
