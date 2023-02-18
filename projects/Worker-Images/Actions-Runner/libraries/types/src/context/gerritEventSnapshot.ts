export interface GerritEventSnapshot {
    type: string
    metadata: {
        projectName: string
        branchName: string
        revision: string
        referenceId: string
        originId?: string
    }
    additionalProperties: {
        workflow?: string
        commit: {
            commit: string;
            parents: {
                commit: string
                subject: string
            }[]
            author: {
                name: string
                email: string
                date: string
                tz: number
            }
            committer: {
                name: string
                email: string
                date: string
                tz: number
            }
            subject: string
            message: string
        }
        files: {
            [filename: string]: {
                "status": "D" | "A" | "M" | string
                "lines_deleted": number
                "size_delta": number
                "size": number
            }
        }
        inputs?: {
            [key: string]: string | number | boolean | undefined
        }
        change?: {
            id: string
            project: string
            branch: string
            hashtags: any[]
            change_id: string
            subject: string
            status: string
            created: string
            updated: string
            submit_type: string
            insertions: number
            deletions: number
            total_comment_count: number
            unresolved_comment_count: number
            has_review_started: boolean
            revert_of: number
            meta_rev_id: string
            _number: number
            owner: {
                _account_id: number
            }
            current_revision: string
            requirements: any[]
            submit_records: {
                rule_name: string
                status: string
                labels: {
                    label: string
                    status: string
                }[]
            }[]
            revision: {
                kind: string
                _number: number
                created: string
                uploader: {
                    _account_id: number
                }
                ref: string
                fetch: {
                    ssh: {
                        url: string
                        ref: string
                    }
                    "anonymous http": {
                        url: string
                        ref: string
                    }
                    http: {
                        url: string
                        ref: string
                    }
                }
            }
        }
    }
}
