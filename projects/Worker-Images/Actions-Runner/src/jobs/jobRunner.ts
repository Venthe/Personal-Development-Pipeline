import { Container, FinalStatus, JobDefinition, JobOutput, RemoteJobDefinition } from '@pipeline/types';
import { ContextManager } from '../context/contextManager';
import { subtitle } from '@pipeline/utilities';
import { StepRunner } from '../steps/stepRunner';
import { renderTemplate } from '../utilities/template';
import { toContainer } from "../utilities/testcontainers";
import { StartedTestContainer } from 'testcontainers';

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
    if (this.jobDefinition?.if && !renderTemplate(this.jobDefinition?.if, this.contextManager.contextSnapshot)) {
      return {
        result: 'skipped'
      };
    }
    const services = Object.keys(this.jobDefinition.services ?? {})
        .map(key => ({definition: this.jobDefinition?.services?.[key], name: key}))
        .filter(a => a.definition !== undefined)
        .map(definition => ({container: toContainer(definition.definition as Container, definition.name), name: definition.name}));
    const startedContainers: StartedTestContainer[] = []
    try {
      for (const container of services) {
        console.log(`Starting service ${container.name}`)
        const startedContainer: StartedTestContainer = await container.container.start();
        const logs = await startedContainer.logs()
        logs
            .on("data", line => process.stdout.write(`[${container.name}] ${line}`))
            .on("err", line => process.stderr.write(`[${container.name}] ${line}`))
            .on("end", () => process.stdout.write(`[${container.name}] Stream closed`));
        startedContainers.push(startedContainer)
      }
      return await this.stepRunner.run();
    } finally {
      for (const container of startedContainers) {
        await container.stop()
      }
    }
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
