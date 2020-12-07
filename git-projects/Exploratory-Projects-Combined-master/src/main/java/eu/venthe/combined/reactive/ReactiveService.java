package eu.venthe.combined.reactive;

import lombok.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class ReactiveService {

    public Mono<TestDto> getSingle(int value) {
        return Mono.just(TestDto.of(value));
    }

    public ParallelFlux<TestDto> getMany(List<Integer> values) {
        return Flux.fromStream(values.stream().map(TestDto::of))
                .parallel()
                .flatMap(a -> Mono.just(a)
                        .delayElement(Duration.ofSeconds(new Random().nextInt(5)))
                        .log()
                );
    }

    @Value(staticConstructor = "of")
    public static class TestDto {
        int value;
    }
}
