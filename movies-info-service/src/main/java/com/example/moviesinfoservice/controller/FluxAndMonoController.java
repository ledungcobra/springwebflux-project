package com.example.moviesinfoservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.just(1, 2, 3, 4)
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> mono() {
        return Mono.just(2222).log();
    }
}
