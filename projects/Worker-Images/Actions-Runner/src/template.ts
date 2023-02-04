import * as nunjucks from 'nunjucks'

nunjucks.configure({ autoescape: true, throwOnUndefined: true, tags: { variableStart: "${{" } })

export const render = (text, context) => nunjucks.renderString(text, context);
