package eu.venthe.pipeline.orchestrator.events.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.events.EventType;
import eu.venthe.pipeline.orchestrator.events.contexts.*;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextBranches;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextPaths;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class PushEventContext extends EventContext implements Event {
    public PushEventContext(ObjectNode root) {
        super(root, EventType.PUSH);
    }

    @Override
    public Map<String, ObjectNode> getNameContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRunName() {
        throw new UnsupportedOperationException();
    }

    /**
     * The SHA of the most recent commit on ref after the push.
     */
    public String getAfter() {
        throw new UnsupportedOperationException();
    }

    public Optional<String> getBaseRef() {
        throw new UnsupportedOperationException();
    }

    /**
     * The SHA of the most recent commit on ref before the push.
     */
    public String getBefore() {
        throw new UnsupportedOperationException();
    }

    /**
     * An array of commit objects describing the pushed commits. (Pushed commits are all commits that are included in the compare between the before commit and the after commit.) The array includes a maximum of 20 commits. If necessary, you can use the Commits API to fetch additional commits. This limit is applied to timeline events only and isn't applied to webhook deliveries.
     */
    public Collection<CommitContext> getCommits() {
        throw new UnsupportedOperationException();
    }

    /**
     * URL that shows the changes in this ref update, from the before commit to the after commit. For a newly created ref that is directly based on the default branch, this is the comparison between the head of the default branch and the after commit. Otherwise, this shows all commits until the after commit.
     */
    public String getCompare() {
        throw new UnsupportedOperationException();
    }

    /**
     * Whether this push created the ref.
     */
    public Boolean getCreated() {
        throw new UnsupportedOperationException();
    }

    /**
     * Whether this push deleted the ref.
     */
    public Boolean getDeleted() {
        throw new UnsupportedOperationException();
    }

    /**
     * An enterprise on GitHub. Webhook payloads contain the enterprise property when the webhook is configured on an enterprise account or an organization that's part of an enterprise account. For more information, see "About enterprise accounts."
     */
    public Optional<EnterpriseContext> getEnterpriseContext() {
        return EnterpriseContext.create(root);
    }

    /**
     * Whether this push was a force push of the ref.
     */
    public Boolean getForced() {
        throw new UnsupportedOperationException();
    }

    public Optional<HeadCommitContext> getHeadCommit() {
        return HeadCommitContext.create(root);
    }

    /**
     * The GitHub App installation. Webhook payloads contain the installation property when the event is configured for and sent to a GitHub App. For more information, see "Using webhooks with GitHub Apps."
     * @return
     */
    public Optional<InstallationContext> getInstallation() {
        return InstallationContext.create(root);
    }

    /**
     * A GitHub organization. Webhook payloads contain the organization property when the webhook is configured for an organization, or when the event occurs from activity in a repository owned by an organization.
     */
    public Optional<OrganizationContext> getOrganization() {
        return OrganizationContext.create(root);
    }

    /**
     * Metaproperties for Git author/committer information.
     */
    public PusherContext getPusher() {
        throw new UnsupportedOperationException();
    }

    /**
     * The full git ref that was pushed. Example: refs/heads/main or refs/tags/v3.14.1.
     */
    public String getRef() {
        return RefContext.ref(root).orElseThrow();
    }

    /**
     * A git repository
     */
    public RepositoryContext getRepository() {
        return RepositoryContext.create(root).orElseThrow();
    }

    /**
     * The GitHub user that triggered the event. This property is included in every webhook payload.
     */
    public SenderContext getSender() {
        return SenderContext.create(root).orElseThrow();
    }

    @Override
    public Boolean matches(OnContextBranches branches) {
        return OnHandlers.branchesMatch(branches, this, this::getRef);
    }

    @Override
    public Boolean matches(OnContextPaths onContextPaths) {
        // FIXME: Add path matching
        return OnHandlers.pathsMatch(onContextPaths, this, Collections::emptyList);
    }
}
