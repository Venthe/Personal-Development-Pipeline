import { renderTemplate, rerenderTemplate } from './template';
import { ContextSnapshot } from '@pipeline/types';

describe('Template', () => {
  it('Mona the Octocat', () => {
    const base = { run: 'echo Hello ${{ inputs.whoToGreet }}.', shell: 'bash' };
    const context = { inputs: { whoToGreet: 'Mona the Octocat' } } as any as ContextSnapshot;
    const result = rerenderTemplate(base, context);

    expect(result)
      .toEqual({ run: 'echo Hello Mona the Octocat.', shell: 'bash' });
  });

  it('Sample string', () => {
    const base = '${{ \'1\' }}';
    const result = renderTemplate(base, {} as any as ContextSnapshot);

    expect(result)
      .toEqual('1');
  });
});

describe('Custom functions', () => {
  describe('success()', () => {
    it('All success', () => {
      const base = '${{ success() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'success' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('All failure', () => {
      const base = '${{ success() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'failure' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('All Cancelled', () => {
      const base = '${{ success() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'cancelled' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('Last failure', () => {
      const base = '${{ success() }}';
      const result = renderTemplate(base, {
        steps: {
          b: { conclusion: 'success' },
          a: { conclusion: 'failure' }
        }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('Last Cancelled', () => {
      const base = '${{ success() }}';
      const result = renderTemplate(base, {
        steps: {
          b: { conclusion: 'success' },
          a: { conclusion: 'cancelled' }
        }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
  });

  describe('failure()', () => {
    it('All success', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'success' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('All failure', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'failure' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('All Cancelled', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, { steps: { a: { conclusion: 'cancelled' } } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('Last failure', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, {
        steps: {
          b: { conclusion: 'success' },
          a: { conclusion: 'failure' }
        }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('Last Cancelled', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, {
        steps: {
          b: { conclusion: 'success' },
          a: { conclusion: 'cancelled' }
        }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('Last job success', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, {
        needs: { previous: { conclusion: 'success' } },
        steps: { a: { conclusion: 'success' } }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('Last job failure', () => {
      const base = '${{ failure() }}';
      const result = renderTemplate(base, {
        needs: { previous: { result: 'failure' } },
        steps: { a: { conclusion: 'success' } }
      } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
  });

  describe('always()', () => {
    it('Any', () => {
      const base = '${{ always() }}';
      const result = renderTemplate(base, {} as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
  });

  describe('cancelled()', () => {
    it('All cancelled', () => {
      const base = '${{ cancelled() }}';
      const result = renderTemplate(base, { job: {status: 'cancelled' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('No cancelled', () => {
      const base = '${{ cancelled() }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
  });

  describe('stringify', () => {
    it('1', () => {
      const base = '${{ job | stringify }}';
      const result = renderTemplate(base, { job: {status: 'cancelled' } } as any as ContextSnapshot);

      expect(result)
          .toEqual("{\"status\":\"cancelled\"}");
    });
  })

  describe('contains', () => {
    it('contains', () => {
      const base = '${{ job.status | contains(\'cancelled\', {test: \'a\'}) }}';
      const result = renderTemplate(base, { job: {status: 'cancelled' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('!contains', () => {
      const base = '${{ not (job.status | contains(\'cancelled\')) }}';
      const result = renderTemplate(base, { job: {status: 'cancelled' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('not contains', () => {
      const base = '${{ job.status | contains(\'cancelled\') }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('case 1', () => {
      const base = '${{ job.status | contains(\'SUCCESS\') }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('case 2', () => {
      const base = '${{ job.status | contains(\'SUCCESS\', {ignoreCase: true}) }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('regexp 1', () => {
      const base = '${{ job.status | contains(\'/^s.c.+$/i\') }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
    it('regexp 2', () => {
      const base = '${{ job.status | contains(\'/^d.c.+$/i\') }}';
      const result = renderTemplate(base, { job: {status: 'success' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(false);
    });
    it('regexp 3', () => {
      const base = '${{ job.status | contains(\'/^s.c.+$/i\') }}';
      const result = renderTemplate(base, { job: {status: 'SUCCESS' } } as any as ContextSnapshot);

      expect(result)
        .toEqual(true);
    });
  });
});