package com.reactive.paradigm.loanbroker.controller;

import com.reactive.paradigm.loanbroker.model.Quotation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@RestController
public class NonBlockingController {
    @GetMapping("nonBlockingGet")
    public Mono<Quotation> getNonBlockingQuote() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Mono.just(new Quotation("Non Blocking Quote", 100D));
    }
}
