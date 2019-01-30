package com.reactive.paradigm.loanbroker;

import com.reactive.paradigm.loanbroker.controller.BankController;
import com.reactive.paradigm.loanbroker.model.Quotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class NonWebClient {

    //@Autowired


    private void getLoanQuotes() {

        BankController bankController = new BankController();

        //bankController.requestForQuotation();

        Flux<String> banksUrl = Flux.just("Bank-1", "Bank-2", "Bank-3");
        Double loanAmount = 1000d;

        Flux.from(banksUrl)
                .flatMap(bankUrl -> {
                    Mono<Quotation> mq = bankController.requestForQuotation(bankUrl, loanAmount);
                    return mq;
                })
                .log("MKKKK")
                //.onErrorResume(ex -> Mono.just(new Quotation("exception mesg", 33D)))
                .blockLast();

    }


    public static void main(String[] args) {
        NonWebClient nonWebClient = new NonWebClient();
        nonWebClient.getLoanQuotes();
    }
}
