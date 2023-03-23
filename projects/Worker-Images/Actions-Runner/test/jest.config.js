const config ={
  displayName: 'Action runner tests',
  testEnvironment: 'node',
  coverageDirectory: 'coverage/libraries/core',
  setupFilesAfterEnv: ['./integration.config.js']
};


module.exports = config;