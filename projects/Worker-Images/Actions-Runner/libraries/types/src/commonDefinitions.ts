export type Defaults = {
    run?: {
        shell?: 'bash'
        workingDirectory?: 'scripts'
    }
};

export type ConcurrencyDefinition = string | { group: string, cancelInProgress: boolean };

export type Permissions = {
    [permissionId: PermissionsScope]: PermissionOption
} | "read-all" | "write-all" | {};
export type PermissionOption = "read" | "write" | "none"

export type PermissionsScope =
    "actions"
    | "checks"
    | "contents"
    | "deployments"
    | "id-token"
    | "issues"
    | "discussions"
    | "packages"
    | "pages"
    | "pull-requests"
    | "repository-projects"
    | "security-events"
    | "statuses"
    | string