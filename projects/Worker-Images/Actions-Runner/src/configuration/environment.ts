import * as fs from 'fs';
import { loadEnvironmentFile } from '../utilities';

export interface RunnerEnvironmentVariables {
  [key: string]: string | undefined;

  RUNNER_CACHE_DIRECTORY: string;
  RUNNER_MANAGER_DIRECTORY: string;
  RUNNER_METADATA_DIRECTORY: string;
  RUNNER_WORKSPACE_DIRECTORY: string;
  RUNNER_BINARIES_DIRECTORY: string;
  RUNNER_SECRETS_DIRECTORY: string;
  RUNNER_ENV_DIRECTORY: string;
  RUNNER_ACTIONS_DIRECTORY: string;
  RUNNER_PIPELINE_DIRECTORY: string;
}

export interface PipelineEnvironmentVariables {
  [key: string]: string | undefined;

  PIPELINE_DEBUG?: string;
  PIPELINE_JOB_NAME: string;
  PIPELINE_BUILD_ID: string;
  PIPELINE_WORKFLOW: string;
  PIPELINE_NEXUS_URL: string;
  PIPELINE_GERRIT_URL: string;
  PIPELINE_DOCKER_URL: string;
  PIPELINE_DOCKER_HOST: string;
}

export type ContextEnvironmentVariables = RunnerEnvironmentVariables & PipelineEnvironmentVariables

export const prepareDefaultEnvironmentVariables = (): RunnerEnvironmentVariables => {
  const runnerEnvs: RunnerEnvironmentVariables = {
    RUNNER_CACHE_DIRECTORY: '/runner/cache',
    RUNNER_MANAGER_DIRECTORY: '/runner',
    RUNNER_PIPELINE_DIRECTORY: '/runner/pipeline',
    RUNNER_METADATA_DIRECTORY: '/runner/metadata',
    RUNNER_BINARIES_DIRECTORY: '/runner/bin',
    RUNNER_WORKSPACE_DIRECTORY: process.env.RUNNER_WORKSPACE_DIRECTORY ?? '/workdir',
    RUNNER_SECRETS_DIRECTORY: '/runner/metadata/secrets',
    RUNNER_ENV_DIRECTORY: '/runner/metadata/env',
    RUNNER_ACTIONS_DIRECTORY: '/runner/actions'
  };

  return {
    ...runnerEnvs,
    ...loadEnvFiles(runnerEnvs.RUNNER_ENV_DIRECTORY
    )
  };
};

function loadEnvFiles(envDirectory: string): { [key: string]: string | undefined } {
  let env: { [key: string]: string | undefined } = {};
  const files = fs.readdirSync(envDirectory);
  files.forEach(file => {
    const parsedEnvironmentFile = loadEnvironmentFile(`${envDirectory}/${file}`);
    Object.keys(parsedEnvironmentFile).forEach((key) => {
      env[key] = parsedEnvironmentFile[key];
    });
  });

  return env;
}
