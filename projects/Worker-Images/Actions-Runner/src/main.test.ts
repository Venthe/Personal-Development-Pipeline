import {main} from './main'

test('logs that we are running', async () => {
    // given
    console.log = jest.fn()

    // when
    await main()

    // then
    expect((console.log as jest.Mock).mock.calls[0][0])
        .toBe("Running actions manager");
});

test.each(["123", "12"])
('Sets build ID from env variable %s', async (buildId) => {
        // given
        console.log = jest.fn()
        process.env.VPIPELINE_BUILD_ID = buildId;

        // when
        await main()

        // then
        expect((console.log as jest.Mock).mock.calls[0][1])
            .toBe(`buildID=${buildId}`);
    }
)
