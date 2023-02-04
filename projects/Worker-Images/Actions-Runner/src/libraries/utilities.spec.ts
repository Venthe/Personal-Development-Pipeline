import {keyValue} from './utilities'

test('KV creates a string equality', async () => {
    expect(keyValue("a","b"))
        .toBe("a=b");
});
