package com.reactive.paradigm.loanbroker.service;

import org.reactivestreams.Subscription;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.BaseSubscriber;

@Service
public class WebClientTransportImpl {

    private final WebClient webClient;

    public WebClientTransportImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .baseUrl("http://localhost:8080/getInfinite")
                .build();

    }

    public  void doGet() {

        MyCustomBackpressureSubscriber myCustomBackpressureSubscriber = new MyCustomBackpressureSubscriber();

        this.webClient.get()
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe(myCustomBackpressureSubscriber);
    }
}

class MyCustomBackpressureSubscriber<T> extends BaseSubscriber<T> {

    int consumed;
    final int limit = 5;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(limit);
    }

    @Override
    protected void hookOnNext(T value) {
        // do business logic there
        System.out.println("value==============" + value + ":       " + consumed);
        consumed++;

        if (consumed == limit) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumed = 0;

            request(limit);
        }
    }


}