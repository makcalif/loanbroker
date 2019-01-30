package com.reactive.paradigm.loanbroker.controller;

import com.reactive.paradigm.loanbroker.model.BestQuotationResponse;
import com.reactive.paradigm.loanbroker.model.Quotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@RestController
public class BankController {
    Logger logger = LoggerFactory.getLogger(BankController.class);

    private static final Quotation QUOTATION_IN_CASE_OF_ERROR = new Quotation("bank-error", Double.MAX_VALUE);

    @GetMapping("/{bank}/quotation") // bank name format is bank-[1-9]
    public Mono<Quotation> quotation (final @PathVariable("bank") String bank, final @RequestParam(value="loanAmount", required = true) Double loanAmount) {
        char bankIndex = bank.charAt(5);

        double interestRate = bankIndex == '5' ? 0.001d : ((double) bankIndex) / 100d;
        logger.info("interest rate for bank {} is {}", bank, interestRate);

        if(bankIndex== '2') {
            return Mono.error(new ServiceUnavailableException("bank-" + bankIndex + " service is unavilable"));
        }

        if (bankIndex == '3') {
            return Mono.delay(Duration.ofMillis(6000))
                    .log()
                    .then(Mono.just(new Quotation("Bank-" + bankIndex, loanAmount)));
        }

        return Mono.just(new Quotation("Bank-" + bankIndex, loanAmount * interestRate));
    }

    @GetMapping("/getBestQuotation")
    public Mono<BestQuotationResponse> getBestQuotation() {
        Flux<String> banksUrl = Flux.just("Bank-1", "Bank-2", "Bank-3");
        Double loanAmount = 1000d;

        return Flux.from(banksUrl)
                .flatMap(bankUrl -> {
                    Mono<Quotation> mq = requestForQuotation(bankUrl, loanAmount)
                     .onErrorResume(e -> Mono.just(QUOTATION_IN_CASE_OF_ERROR));
                    return mq;
                })
                .log()
                .filter(offer -> {
                    logger.info("offer is {} ", offer);
                    return !offer.equals(QUOTATION_IN_CASE_OF_ERROR);
                })
                .collect(() -> new BestQuotationResponse(loanAmount), BestQuotationResponse::offer)
                .doOnSuccess(BestQuotationResponse::finish)
                .flatMap(bqr -> {
                       return Mono.justOrEmpty(selectBestQuotation(bqr.getOffers()))
                            .map(bestQuotation -> {
                                bqr.bestOffer(bestQuotation);
                                return bqr;
                            });
                })
                .timeout(Duration.ofMillis(4000)) // propagate a timeout exception as soon as no item is emitted
                .single();
    }

    private Optional<Quotation> selectBestQuotation(List<Quotation> quotations){
        return  Optional.ofNullable(quotations)
                .flatMap( _quotations -> _quotations.stream().sorted((q1, q2) -> (q1.getOffer() > q2.getOffer() ? 1:-1))
                        .findFirst());
    }

    Mono<Quotation> requestForQuotation(String bankUrl, Double loanAmount) {

        Function timeoutHandler = (clientResp) -> { return ClientResponse.create(HttpStatus.BAD_GATEWAY); };
        return WebClient.create().get()
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
                .timeout(Duration.ofSeconds(3), Mono.empty());

    }

}
