package com.reactive.paradigm.loanbroker.controller;

import com.reactive.paradigm.loanbroker.model.Quotation;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BlockingVsNonBlockingTest {

    @Test
    public void testNonBlocking() throws InterruptedException {
//         Quotation quotation =
        Mono<Quotation> quotationMono =
                 WebClient.create().get()
        .uri( uriBuilder ->
                uriBuilder
                .scheme("http")
                .port(8080)
                .host("localhost")
                .path("/nonBlockingGet")
                .build()
        )
        .retrieve()
        .bodyToMono(Quotation.class);

        quotationMono.subscribe( q -> {
            System.out.println("non blocking quote is :" +q + " thread:" + Thread.currentThread().getName());
        });
        System.out.println("subscriber invoked-------------------- thread:"+ Thread.currentThread().getName());
        Thread.sleep(4000);

    }

    @Test
    public void testBlocking() throws InterruptedException {

        RestTemplate restTemplate = new RestTemplate();
        System.out.println("api get invoked--------------------");
        Quotation quotation =
                restTemplate.getForObject("http://localhost:8080/blockingGet",
                        Quotation.class);


        System.out.println("  blocking quote is :" +quotation + " thread:" + Thread.currentThread().getName());
        System.out.println("after getting blocking quote-------------------- thread:" + Thread.currentThread().getName());

    }
}
