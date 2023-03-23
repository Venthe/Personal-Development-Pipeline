import { shell } from '@pipeline/process';
import { callbacks, context, step } from '@pipeline/core';

(async function() {
  console.log('Console.log: Hello world!', step);
  await shell('ls && pwd');

  callbacks.sendOutput('sample', { a: 1, b: 2 });
  callbacks.addEnv('CHILD_ENV', '123');
  callbacks.addToPath('/home/root/.bin/binbin');
})();