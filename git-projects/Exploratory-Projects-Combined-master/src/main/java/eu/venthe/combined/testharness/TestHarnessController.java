package eu.venthe.combined.testharness;

import eu.venthe.combined.customer.Customer;
import eu.venthe.combined.customer.CustomerRepository;
import eu.venthe.combined.utils.BatchProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test-harness")
public class TestHarnessController {
    private final CustomerRepository customerRepository;
    private final BatchProcessor batchProcessor;

    @PostMapping("/start")
    private void start() {
        customerRepository.deleteAll();

        batchProcessor.iterate(
                "testHarness",
                IntStream.range(0, 1000000).boxed().collect(Collectors.toList()),
                value -> {
                    if (new Random().nextDouble() < 0.05)
                        throw new RuntimeException("Simulated error");
                    return Customer.of(value.toString(), (long) value);
                },
                customerRepository::saveAll,
                100,
                "correlationId"
        );
    }

}
