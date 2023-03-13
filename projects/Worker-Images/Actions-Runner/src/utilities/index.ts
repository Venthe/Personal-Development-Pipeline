import { shellMany } from '@pipeline/process';

export * from './filesystem';

export const normalizeEvent = (input: string) => input.replace('-', '').toLowerCase();

export const configureGit = async () => {
  await shellMany([
    'git config --global init.defaultBranch main',
    'git config --global advice.detachedHead false',
    `git config --global user.name "Action runner"`,
    `git config --global user.email "jenkins@home.arpa"`,
    `git config --global --add safe.directory ${process.cwd()}`
  ]);
};

export const exceptionMapper = (exception: any) => `${exception?.error ?? exception?.message ?? 'unknown'}: ${exception.stack}`;
