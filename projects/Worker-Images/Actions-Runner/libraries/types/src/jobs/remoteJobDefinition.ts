export type RemoteJobDefinition = {
    /**
     * The location and version of a reusable workflow file to run as a job. Use one of the following syntaxes:
     *
     *     {owner}/{repo}/.github/workflows/{filename}@{ref} for reusable workflows in public and private repositories.
     *     ./.github/workflows/{filename} for reusable workflows in the same repository.
     *
     * {ref} can be a SHA, a release tag, or a branch name. Using the commit SHA is the safest for stability and
     * security. For more information, see "Security hardening for GitHub Actions." If you use the second syntax
     * option (without {owner}/{repo} and @{ref}) the called workflow is from the same commit as the caller workflow.
     */
    uses: string
    /**
     * When a job is used to call a reusable workflow, you can use with to provide a map of inputs that are passed to
     * the called workflow.
     *
     * Any inputs that you pass must match the input specifications defined in the called workflow.
     *
     * Unlike jobs.<job_id>.steps[*].with, the inputs you pass with jobs.<job_id>.with are not be available as
     * environment variables in the called workflow. Instead, you can reference the inputs by using the inputs context.
     */
    with: { [inputId: string]: string }
    /**
     * When a job is used to call a reusable workflow, you can use secrets to provide a map of secrets that are passed
     * to the called workflow.
     *
     * Any secrets that you pass must match the names defined in the called workflow.
     */
    secrets: { [inputId: string]: string } | "inherit"
};