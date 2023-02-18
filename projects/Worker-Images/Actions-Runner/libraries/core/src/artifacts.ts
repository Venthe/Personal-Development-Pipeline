import { password, shell, ShellCallbacks } from '@pipeline/process';
import { ContextSnapshot, GerritEventSnapshot, SecretsSnapshot } from '@pipeline/types';

export enum RepositoryType {
  User,
  System
}

function nexusBasicAuth(secrets: SecretsSnapshot) {
  return `${secrets.NEXUS_USERNAME}:${secrets.NEXUS_PASSWORD}`;
}

type DownloadParameters = { sourcePath: string, targetPath: string, type?: RepositoryType, context: ContextSnapshot };
type UploadParameters = { sourcePath: string, targetPath?: string, type?: RepositoryType, context: ContextSnapshot };

const curlPasswordMask = () => ({ mask: [password('--user')] });

interface Options {
  silent?: boolean;
  hack?: boolean;
}

export const download = async ({
                                 sourcePath,
                                 targetPath,
                                 context,
                                 type = RepositoryType.User
                               }: DownloadParameters, { silent = false, hack = true }: Options = {}) => {
  if (hack) await hackLoginFail(context);

  const nexusPath = pathStrategy(context, sourcePath, type);
  const url = `${context.internal.nexusUrl}/${nexusPath}`;
  if (!silent) console.debug('Downloading artifact', nexusPath, 'to', targetPath);
  return shell(`curl ${silent ? '--silent' : ''} --fail --user ${(nexusBasicAuth(context.secrets))} ${url} --output ${targetPath}`, { silent, ...curlPasswordMask() });
};

export const upload = async ({
                               sourcePath,
                               targetPath,
                               context,
                               type = RepositoryType.User
                             }: UploadParameters, { silent = false, hack = false }: Options = {}) => {
  if (hack) await hackLoginFail(context);

  const nexusPath = pathStrategy(context, targetPath ?? sourcePath, type);
  const url = `${context.internal.nexusUrl}/${nexusPath}`;
  if (!silent) console.debug('Uploading artifact', sourcePath, 'to', nexusPath);
  return shell(`curl ${silent ? '--silent' : ''} --fail --user ${(nexusBasicAuth(context.secrets))} --upload-file "${sourcePath}" "${url}"`, { silent, ...curlPasswordMask() });
};

const pathStrategy = (context: ContextSnapshot, sourcePath, type?: RepositoryType) => {
  const event: GerritEventSnapshot = context.internal.event;
  return type === RepositoryType.User
    ? `${['pipeline', event.metadata.projectName, event.metadata.branchName, sourcePath].join('/')}`
    : `${sourcePath}`;
};

// For some reason, first curl fails with 401
const hackLoginFail = async (context: ContextSnapshot) => {
  try {
    console.debug("Hack start")
    let tmp: string = '';
    await shell('mktemp', { silent: true, callbacks: { stdout: (t) => tmp = t.chunk } });
    await upload({ sourcePath: tmp, context }, { silent: true, hack: false });
    await download({ sourcePath: tmp, targetPath: tmp, context }, { silent: true, hack: false });
    console.debug("Hack End")
  } catch (e) {
    console.debug("Hack worked!", JSON.stringify(e))
  }
};
