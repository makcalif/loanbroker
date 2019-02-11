package com.reactive.paradigm.loanbroker;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

public class NonWebClientWithBackPressure {

     private void getTweetsWithBackPressure() {
            // doesn't work for some reason
           WebClient.create().get()
                 .uri(builder -> builder.scheme("http")
                         .port(8080)
                         .host("localhost").path("/getInfinite")
                         .build())
                   .accept(TEXT_EVENT_STREAM)
                 .retrieve()

                 //Note that Unlike retrieve() method, the exchange() method does not throw exceptions
                 // in case of 4xx or 5xx responses. You need to check the status codes yourself and handle
                 // them in the way you want to.
                 .onStatus(HttpStatus::is5xxServerError , clientResponse -> {
                     return clientResponse.bodyToMono(String.class).map(body -> new RuntimeException(body));
                 })
                 .bodyToMono(String.class)
                   .log()
                 .subscribe(System.out::println);

    }


    public static void main(String[] args) {
        NonWebClientWithBackPressure nonWebClient = new NonWebClientWithBackPressure();
        nonWebClient.getTweetsWithBackPressure();
    }
}
