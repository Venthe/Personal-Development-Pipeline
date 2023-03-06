export function sets<T, U, W, V>(current: string[],
                                 expected: string[],
                                 mappers?: {
                                     createdStateMapper?: (string) => T,
                                     expectedStateMapper?: (string) => U,
                                     currentStateMapper?: (string) => W,
                                     toRemoveMapper?: (string) => V
                                 }): {
    toCreate: (string | T)[],
    toRemove: (string | V)[],
    toSynchronize: (string | {
        currentState: undefined | W,
        expectedState: U
    })[]
} {
    const _current = [...new Set(current)]
    const _expected = [...new Set(expected)]

    const toCreate = _expected.filter(e => !_current.includes(e))
    const toRemove = _current.filter(e => !_expected.includes(e))
    const toSynchronize = _current.filter(e => _expected.includes(e))

    return {
        toCreate: toCreate.map(tc => mappers?.createdStateMapper?.(tc) ?? tc),
        toRemove: toRemove.map(tc => mappers?.toRemoveMapper?.(tc) ?? tc),
        toSynchronize: toSynchronize.map(tc => mappers?.expectedStateMapper ? {
            currentState: mappers?.currentStateMapper?.(tc) ?? undefined,
            expectedState: mappers?.expectedStateMapper(tc)
        } : tc)
    }
}

export const groupBy = (array, key) => array.reduce((hash, obj) => {
    if (obj[key] === undefined) return hash;
    return Object.assign(hash, {[obj[key]]: (hash[obj[key]] || []).concat(obj)})
}, {});
