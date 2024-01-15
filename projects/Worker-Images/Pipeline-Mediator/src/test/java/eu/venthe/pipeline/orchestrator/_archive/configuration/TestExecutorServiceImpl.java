/*
package eu.venthe.pipeline.orchestrator.configuration;

import eu.venthe.pipeline.orchestrator.scratch.workflows.ports.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
@Slf4j
public class TestExecutorServiceImpl implements ExecutorService {
    private final java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(1);

    @Override
    public void execute(String jobId) {
        executor.execute(() -> {
            run(jobId);
        });
    }

    private void run(String jobId) {
        log.debug("Executing job: {}", jobId);
    }
}
*/
