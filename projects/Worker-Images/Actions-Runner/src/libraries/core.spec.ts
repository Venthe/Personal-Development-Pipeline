import { getStep, getContext } from './core';

describe('core', () => {
    it('should work', () => {
        expect(getStep()).toBeUndefined();
    });
    it('should work', () => {
        expect(getContext()).toBeUndefined();
    });
});
