import process from 'process';
import { ActionStepDefinition, ContextSnapshot } from '@pipeline/types';

export type Core = {
  context: ContextSnapshot;
  step: ActionStepDefinition;
  callbacks: {
    sendOutput: (key: string, value: any) => void
    addEnv: (key: string, value: any) => void
    addToPath: (path: string) => void
  };
};

export type CoreArguments = [step: ActionStepDefinition, context: ContextSnapshot];

export enum MessageType {
  ADD_TO_PATH = 'addToPath',
  ADD_ENV = 'addEnv',
  SET_OUTPUT = 'setOutput',
}

export const { step, context, callbacks }: Core = (() => {
  const [step, context]: CoreArguments = JSON.parse(process.argv[2] ?? '[{},{}]');
  return {
    step: step,
    context,
    callbacks: {
      addToPath: (path: string) => {
        process.env['PATH'] = `${path}:${process.env['PATH']}`;
        return process.send?.(message<AddToPathMessage>({ type: MessageType.ADD_TO_PATH, content: path }));
      },
      addEnv: (env: string, value: string) => {
        process.env[env] = value;
        return process.send?.(message<AddEnvMessage>({ type: MessageType.ADD_ENV, content: { env, value } }));
      },
      sendOutput: (key: string, value: number | string) => process.send?.(message<SetOutputMessage>({
        type: MessageType.SET_OUTPUT,
        content: { key, value }
      }))
    }
  };
})();

export interface Message<U extends MessageType, T> {
  type: U;
  content: T;
}

export type SetOutputMessage = Message<MessageType.SET_OUTPUT, { key: string, value: number | string }>
export type AddEnvMessage = Message<MessageType.ADD_ENV, { env: string, value: string }>
export type AddToPathMessage = Message<MessageType.ADD_TO_PATH, string>

const message = <T extends Message<MessageType, any>>(message: T): string => (JSON.stringify(message));

(() => {
    if (process.env.PIPELINE_DEBUG !== '1') {
        console.debug = () => {
        };
    }
})();
