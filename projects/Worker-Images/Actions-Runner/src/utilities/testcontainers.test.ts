import {mapPortToPortWithOptionalBinding, mapVolume} from "./testcontainers";

describe('Containers', () => {
    test.each([
        [15, 15],
        ["15", 15],
        ["16:15", {host: 16, container: 15}]
    ])('Map ports', (a, b) => {
        expect(mapPortToPortWithOptionalBinding(a))
            .toEqual(b);
    });

    test.each([
        ["a:b", {source: "a", target: "b"}],
        ["a:b:ro", {source: "a", target: "b", mode: "ro"}]
    ])('Map ports', (a, b) => {
        expect(mapVolume(a))
            .toEqual(b);
    });
});
