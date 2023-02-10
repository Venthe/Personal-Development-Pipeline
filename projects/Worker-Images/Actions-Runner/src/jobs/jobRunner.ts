import { FinalStatus, JobDefinition, JobOutput, RemoteJobDefinition } from '@pipeline/types';
import { ContextManager } from '../context/contextManager';
import { subtitle } from '@pipeline/utilities';
import { StepRunner } from '../steps/stepRunner';

export interface SingleJobResult {
  result: FinalStatus,
  outputs?: JobOutput
}

export class JobRunner {
  private readonly jobManager: JobManager;

  constructor(readonly job: JobDefinition | RemoteJobDefinition,
              private readonly contextManager: ContextManager) {

    this.jobManager = this.mapJobToManger(job);
  }

  mapJobToManger(job: JobDefinition | RemoteJobDefinition): JobManager {
    console.debug(JSON.stringify(job));
    return (job as any).steps !== undefined
      ? new LocalJobManager(job as JobDefinition, this.contextManager)
      : new RemoteJobManager(job as RemoteJobDefinition);
  }

  async run(): Promise<SingleJobResult> {
    console.log(subtitle(`Running job ${this.contextManager.contextSnapshot.internal.job}`));
    return await this.jobManager.run();
  }
}

interface JobManager {
  run(): Promise<SingleJobResult>;
}

class LocalJobManager implements JobManager {
  private stepRunner: StepRunner;

  constructor(private readonly jobDefinition: JobDefinition,
              private readonly contextManager: ContextManager) {
    this.stepRunner = StepRunner.forJob(this.jobDefinition, contextManager);
  }

  async run(): Promise<SingleJobResult> {
    return await this.stepRunner.run();
  }

}

class RemoteJobManager implements JobManager {
  constructor(private readonly jobDefinition: RemoteJobDefinition) {
    throw new Error('Unsupported operation exception');
  }

  async run(): Promise<SingleJobResult> {
    throw new Error('Unsupported operation exception');
  }
}