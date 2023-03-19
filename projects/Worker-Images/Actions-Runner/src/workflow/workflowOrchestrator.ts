import { ContextSnapshot, Workflow } from '@pipeline/types';
import { JobRunner, SingleJobResult } from '../jobs/jobRunner';
import { ContextManager } from '../context/contextManager';
import { loadYamlFile, normalizeEvent } from '../utilities';
import { PipelineEnvironmentVariables, prepareDefaultEnvironmentVariables } from '../configuration/environment';
import {shell, shellMany} from '@pipeline/process';
import { title } from '@pipeline/utilities';
import { renderTemplate } from 'utilities/template';
import {checkoutCommands} from "@pipeline/core";

export class WorkflowOrchestrator {
  private readonly contextManager: ContextManager;

  private workflow: Workflow | undefined;
  private jobRunner: JobRunner | undefined;

  public static async create(env: PipelineEnvironmentVariables) {
    const workflowOrchestrator = new WorkflowOrchestrator(env);
    await workflowOrchestrator.postConstruct();
    return workflowOrchestrator;
  }

  private constructor(readonly env: PipelineEnvironmentVariables) {
    this.contextManager = ContextManager.forWorkflow({ environmentVariables: { ...env, ...prepareDefaultEnvironmentVariables() } });
  }

  private async postConstruct() {
    await this.createRequiredDirectories();
    await WorkflowOrchestrator.downloadPipelines(this.contextManager.contextSnapshot);
    this.workflow = WorkflowOrchestrator.loadWorkflow(this.contextManager.contextSnapshot);
    this.contextManager.appendEnvironmentVariables(this.workflow.env);

    console.log(title(`Initializing workflow`));
    console.log(` Workflow name: ${this.workflow.name}`);
    console.log(` Workflow run name: ${this.workflowRunName}`);

    this.jobRunner = this.prepareJobRunner(this.contextManager.contextSnapshot.internal.job, this.contextManager);
  }

  private async createRequiredDirectories() {
    await shellMany(this.contextManager.getRunnerDirectories().map(directory => `mkdir --parents ${directory}`), { silent: true });
  }

  private static async downloadPipelines(contextSnapshot: ContextSnapshot) {
    const projectUrl = `${contextSnapshot.internal.projectUrl}/${contextSnapshot.internal.event.metadata.projectName}`;
    await shellMany(checkoutCommands(
        {
          repository: projectUrl,
          revision: contextSnapshot.internal.event.metadata.revision,
          options: {
            branchName: contextSnapshot.internal.event.metadata.branchName,
            depth: 1,
            quiet: false,
            sparseCheckout: [".pipeline/"]
          }
        }
    ), { cwd: contextSnapshot.internal.pipelinesDirectory });
  }

  private static loadWorkflow = (contextSnapshot: ContextSnapshot) => (loadYamlFile<Workflow>(`${contextSnapshot.internal.pipelinesDirectory}/.pipeline/workflows/${contextSnapshot.internal.workflow}`));

  get workflowRunName() {
    const contextSnapshot = this.contextManager.contextSnapshot;
    if (this.workflow?.runName) {
      return renderTemplate(this.workflow.runName, contextSnapshot);
    } else {
      switch (normalizeEvent(contextSnapshot.internal.eventName)) {
        case normalizeEvent('patchset-created'):
        case normalizeEvent('change-merged'):
          return contextSnapshot.internal.event.additionalProperties.commit.subject;
        default:
          return normalizeEvent(contextSnapshot.internal.eventName);
      }
    }
  }

  private prepareJobRunner = (jobName: string, contextManager: ContextManager) => {
    if (!this.workflow) {
      throw new Error('Workflow should exist!');
    }
    return new JobRunner(this.workflow.jobs[jobName], contextManager);
  };

  public async run(): Promise<SingleJobResult> {
    if (!this.jobRunner) {
      throw new Error('Job runner should exist!');
    }
    return await this.jobRunner.run();
  }
}
