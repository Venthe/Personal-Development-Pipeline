/*
package eu.venthe.pipeline.orchestrator.configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.scratch.events.ports.EventSchedulerService;
import eu.venthe.pipeline.orchestrator.scratch.workflows.EventHandlingService;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class TestEventSchedulerServiceImpl implements EventSchedulerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final Queue<Envelope> events = new ArrayBlockingQueue<>(100, true);
    private final EventHandlingService eventHandlingService;

    public TestEventSchedulerServiceImpl(EventHandlingService eventHandlingService) {
        this.eventHandlingService = eventHandlingService;
        executor.execute(() -> {
            log.debug("Starting event listener");
            try {
                while (true) {
                    Thread.sleep(100);
                    Optional.ofNullable(events.poll())
                            .ifPresent(ev -> {
                                log.debug("Event found, handling. {}", ev);
                                this.eventHandlingService.handle(ev.getObjectNode());
                            });
                }
            } catch (InterruptedException e) {
                log.error("", e);
                executor.shutdown();
            }
        });
    }

    @Override
    public String schedule(ObjectNode event) {
        Envelope e = new Envelope(event);
        log.debug("Event scheduled for execution");
        events.add(e);
        return e.getCorrelationId();
    }


    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @RequiredArgsConstructor
    class Envelope {
        @EqualsAndHashCode.Include
        String correlationId = UUID.randomUUID().toString();
        ObjectNode objectNode;
    }
}
*/
