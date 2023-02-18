import {ContextSnapshot} from '@pipeline/types';
import {shell, shellMany} from '@pipeline/process';

type CheckoutParams = { project: string, revision: string, cwd: string, directory?: string, silent?: boolean };

export type CheckoutType = (params: CheckoutParams, context: ContextSnapshot) => Promise<void>;

export const checkout: CheckoutType = async ({project, revision, cwd, directory, ...rest}, context) => {
    const finalCwd = (cwd ?? process.cwd) + (directory ? `/${directory}` : '');
    await shell(`mkdir --parents ${finalCwd}`, {silent: rest.silent ?? false});

    console.debug(`Downloading from Gerrit: ${project}, ${revision}, ${cwd}`);
    let projectUrl = `${(context.internal.gerritUrl)}/${project}`;
    await shellMany(checkoutCommands({
        repository: projectUrl,
        revision
    }), {cwd: finalCwd, ...{silent: rest.silent ?? false}});
};

export const checkoutCommands = ({
                                     options,
                                     revision,
                                     repository
                                 }: {
    repository: string,
    revision: string,
    options?: {
        sparseCheckout?: string[],
        depth?: number,
        branchName?: string,
        initialRefspec?: string,
        quiet?: boolean
    }
}): string[] => {
    const depth = options?.depth ? `--depth=${options?.depth}` : '';
    const initialRefspec = `'${options?.initialRefspec ? options.initialRefspec : "+refs/heads/*:refs/remotes/origin/*"}'`;
    const verbosity = options?.quiet ? '--quiet' : "--progress";
    const sparseCheckout = [
        ...(options?.sparseCheckout || [])
            .map(directory => `git sparse-checkout set "${directory}" --no-cone`)
    ];
    return [
        `git init .`,
        `git fetch --tags --force ${verbosity} ${depth} -- ${repository} ${initialRefspec}`,
        `git config remote.origin.url ${repository}`,
        `git config --add remote.origin.fetch ${initialRefspec}`,
        `git fetch --tags --force ${verbosity} ${depth} -- ${repository} '${revision}'`,
        ...sparseCheckout,
        `git checkout ${verbosity} FETCH_HEAD ${options?.branchName ? `-B ${options.branchName}` : '--force'}`
    ];
}