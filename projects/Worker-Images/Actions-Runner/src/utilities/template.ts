import * as nunjucks from 'nunjucks';
import { ContextSnapshot, NeedsSnapshot, StepsResultSnapshot } from '@pipeline/types';

const env = new nunjucks.Environment(undefined, {
  autoescape: true,
  throwOnUndefined: true,
  tags: { variableStart: '${{' }
});

const isRegExp = (string) => {
  try {
    return new Function(`
            "use strict";
            try {
                new RegExp(${string});
                return true;
            } catch (e) {
                return false;
            }
        `)();
  } catch(e) {
    return false;
  }
};

function stringToRegex(str) {
  const match = str.match(/^([\/~@;%#'])(.*?)\1([gimsuy]*)$/);
  return match ?
    new RegExp(
      match[2],
      match[3]
        // Filter redundant flags, to avoid exceptions
        .split('')
        .filter((char, pos, flagArr) => flagArr.indexOf(char) === pos)
        .join('')
    )
    : new RegExp(str);
}

export const renderTemplate = (text: string, context: ContextSnapshot): any => {
  try {
    env.addFilter('contains', (text, searchedText: string | RegExp, options?: { ignoreCase?: boolean }) => {
      if (!isRegExp(searchedText)) {
        const parser = value => options?.ignoreCase ? value.toLowerCase() : value;
        return parser(text).includes(parser(searchedText));
      } else {
        return !!text.match(stringToRegex(searchedText))
      }
    });
    env.addGlobal('success', () => {
      const steps: StepsResultSnapshot = context.steps || {};
      return (Object.keys(steps).map(a => steps[a]).map(a => a.conclusion).filter(a => ['failure', 'cancelled'].includes(a)).length === 0);
    });
    env.addGlobal('always', () => {
      return true;
    });
    env.addGlobal('cancelled', () => {
      return context.job?.status === 'cancelled'
    });
    env.addGlobal('failure', () => {
      const steps: StepsResultSnapshot = context.steps || {};
      const needs: NeedsSnapshot = context.needs || {};
      return Object.keys(steps).map(a => steps[a]).map(a => a.conclusion).filter(a => ['failure', 'cancelled'].includes(a)).length > 0
        || Object.keys(needs).map(a => needs[a]).map(a => a.result).filter(a => ['failure', 'cancelled'].includes(a)).length > 0;
    });
    const result = env.renderString(text, context);
    if (result === 'true') {
      return true;
    } else if (result === 'false') {
      return false;
    } else {
      return result;
    }
  } catch (e: any) {
    // TODO: Traverse tree, find secrets, mask them
    e.message = (e.message ?? '') + `: ${text}, ${JSON.stringify(context, undefined, 2)}`;
    throw e;
  }
};
export const rerenderTemplate = <T extends object | string = object>(object: T, context: ContextSnapshot): T => JSON.parse(renderTemplate(JSON.stringify(object), context));