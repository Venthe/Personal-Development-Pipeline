package eu.venthe.combined.api;

import eu.venthe.combined.reactive.ReactiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reactive-manager")
public class ReactiveController {
    private final ReactiveService reactiveService;

    @GetMapping("/single/{value}")
    public Mono<ReactiveService.TestDto> getUser(@PathVariable Integer value) {
        return reactiveService.getSingle(value);
    }

    @GetMapping("/many")
    public ParallelFlux<ReactiveService.TestDto> getUsers(@RequestBody List<Integer> many) {
        return reactiveService.getMany(many);
    }
}
