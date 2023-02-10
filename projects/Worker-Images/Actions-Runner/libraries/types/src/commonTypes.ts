/**
 * success()
 * always()
 * cancelled()
 * failure()
 *
 * js code
 * context?
 */
export type Expression = string

export type InputOutput = string | number | boolean | undefined

export type CurrentStatus = "success" | "failure" | "cancelled"
export type FinalStatus = CurrentStatus | "skipped"