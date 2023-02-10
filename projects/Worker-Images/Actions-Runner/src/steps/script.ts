import { renderTemplate } from '../utilities/template';
import { ContextSnapshot, Expression } from '@pipeline/types';

export const shouldRunExpression = (contextSnapshot: ContextSnapshot, expression?: Expression): boolean | undefined => {
  const stronglyTypedScriptRender = (input: string) => {
    const result = renderTemplate(input, contextSnapshot);

    if (!(result === true || result === false)) {
      throw new Error('Invalid script result!');
    }

    return result;
  };
  return expression ? stronglyTypedScriptRender(expression) : undefined;
};