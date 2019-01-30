package com.reactive.paradigm.loanbroker;

import com.reactive.paradigm.loanbroker.model.Quotation;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class NonWebClient {

    public static void main(String[] args) {

//        Flux<String> banksUrl = Flux.just("Bank-1", "Bank-2", "Bank-3");
        Double loanAmount = 1000d;
//
//        Flux.from(banksUrl)
//                .flatMap(bankUrl -> {
//                    Mono<Quotation> mq =

        String bankUrl = "Bank-3";

        WebClient.create().get()
                .uri(builder -> builder.scheme("http")
                        .port(8080)
                        .host("localhost").path(bankUrl + "/quotation")
                        .queryParam("loanAmount", loanAmount)
                        .build())
                .retrieve()
//                .onStatus(HttpStatus::isError, () -> Mono.just(new Quotation("itmoutout", 33d)))
//                .onStatus(HttpStatus::isError, timeoutHandler)
//                .onStatus(HttpStatus::is5xxServerError, timeoutHandler)
                .bodyToMono(Quotation.class)
//                .timeout(Duration.ofSeconds(3), Mono.just(new Quotation("timeout-bank", 55d)));
                .timeout(Duration.ofSeconds(8), Mono.empty())
                .subscribe(System.out::println);
//                .block();
        System.out.println("this is last =============================");

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
