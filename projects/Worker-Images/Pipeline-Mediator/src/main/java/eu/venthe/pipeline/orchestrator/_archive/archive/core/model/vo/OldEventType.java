/*
package eu.venthe.pipeline.orchestrator.archive.core.model.vo;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OldEventType {
    public static final String WORKFLOW_DISPATCH = "workflow-dispatch";
    public static final String WORKFLOW_CALL = "workflow-call";
    public static final String ASSIGNEE_CHANGED = "assignee-changed";
    public static final String CHANGE_ABANDONED = "change-abandoned";
    public static final String CHANGE_DELETED = "change-deleted";
    public static final String CHANGE_MERGED = "change-merged";
    public static final String CHANGE_RESTORED = "change-restored";
    public static final String COMMENT_ADDED = "comment-added";
    public static final String DROPPED_OUTPUT = "dropped-output";
    public static final String PROJECT_CREATED = "project-created";
    public static final String PATCHSET_CREATED = "patchset-created";
    public static final String REF_UPDATED = "ref-updated";
    public static final String REVIEWER_ADDED = "reviewer-added";
    public static final String REVIEWER_DELETED = "reviewer-deleted";
    public static final String TOPIC_CHANGED = "topic-changed";
    public static final String WIP_STATE_CHANGED = "wip-state-changed";
    public static final String PRIVATE_STATE_CHANGED = "private-state-changed";
    public static final String VOTE_DELETED = "vote-deleted";
    public static final String PROJECT_HEAD_UPDATED = "project-head-updated";

    public static List<String> CHANGE_EVENTS = List.of(
            ASSIGNEE_CHANGED,
            CHANGE_MERGED,
            CHANGE_RESTORED,
            COMMENT_ADDED,
            PATCHSET_CREATED,
            REVIEWER_ADDED,
            REVIEWER_DELETED,
            TOPIC_CHANGED,
            WIP_STATE_CHANGED,
            VOTE_DELETED,
            PRIVATE_STATE_CHANGED
    );
    public static List<String> DISPATCH_EVENTS = List.of(
            WORKFLOW_CALL,
            WORKFLOW_DISPATCH
    );
    public static List<String> REVISION_EVENTS = List.of(
            REF_UPDATED
    );
    public static List<String> IGNORED_EVENTS = List.of(
            DROPPED_OUTPUT,
            CHANGE_DELETED,
            CHANGE_ABANDONED,
            PROJECT_HEAD_UPDATED,
            PROJECT_CREATED
    );
}
*/
