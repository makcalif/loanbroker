package com.reactive.paradigm.loanbroker.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class InfiniteFluxController {

    int tweetCount =0;

    @GetMapping(value = "/getInfinite", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getInfiniteWithCreate() {
        return Flux.<String>generate(sink -> {
            System.out.println("Generating tweet #:" + tweetCount++);
            sink.next("tweet #:" + tweetCount);
        })
        .delayElements(Duration.ofMillis(100));
    }

    // how can we use this to feed sink??
    private Flux<String> getFlux() {
        System.out.println("generating tweet ");
        Flux<Long> inteval = Flux.interval(Duration.ofMillis(100));
        Flux<String> f = Flux.just("tweet from flux").zipWith( inteval, (str, dly) -> str + dly);
        return f;
    }

}