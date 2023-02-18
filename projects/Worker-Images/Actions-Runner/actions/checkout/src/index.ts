import {checkoutCommands, context} from '@pipeline/core';
import {shellMany} from '@pipeline/process';

(async () => {
    const gerritURL = `${context.internal.gerritUrl}/${context.internal.event.metadata.projectName}`;
    const event: any = context.internal.event;
    await shellMany(checkoutCommands({
        repository: gerritURL,
        revision: event.metadata.revision,
        options: {
            depth: 1,
            branchName: event.metadata.branchName
        }
    }));
})();