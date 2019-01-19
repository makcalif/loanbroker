package com.reactive.paradigm.loanbroker.controller;

import com.reactive.paradigm.loanbroker.model.BestQuotationResponse;
import com.reactive.paradigm.loanbroker.model.Quotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

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
            return Mono.delay(Duration.ofMillis(2000)).then(Mono.just(new Quotation("Bank-" + bankIndex, loanAmount)));
        }

        return Mono.just(new Quotation("Bank-" + bankIndex, loanAmount * interestRate));
    }

    @GetMapping("/getBestQuotation")
    public Mono<BestQuotationResponse> getBestQuotation() {
        Flux<String> banksUrl = Flux.just("Bank-1", "Bank-2", "Bank-3");
        Double loanAmount = 1000d;


        return Flux.from(banksUrl)
                .flatMap(bankUrl -> requestForQuotation(bankUrl, loanAmount)
                        .onErrorReturn(QUOTATION_IN_CASE_OF_ERROR))
                .filter(offer -> !offer.equals(QUOTATION_IN_CASE_OF_ERROR))
                .collect(() -> new BestQuotationResponse(loanAmount), BestQuotationResponse::bestOffer)
                .doOnSuccess(BestQuotationResponse::finish)
                .flatMap(bqr -> Mono.justOrEmpty(selectBestQuotation(bqr.getOffers()))
                        .map(bestQuotation -> {
                            bqr.bestOffer(bestQuotation);
                            return bqr;
                        }))
                .timeout(Duration.ofMillis(3000))
                .single();
    }

    private Optional<Quotation> selectBestQuotation(List<Quotation> quotations){
        return  Optional.ofNullable(quotations)
                .flatMap( _quotations -> _quotations.stream().sorted((q1, q2) -> (q1.getOffer() > q2.getOffer() ? 1:-1))
                        .findFirst());
    }

    Mono<Quotation> requestForQuotation(String bankUrl, Double loanAmount) {
//        ClientRequest<Void> requet = ClientRequest.create(HttpMethod.GET, bankUrl)
//                .
//                );

        WebClient webClient = WebClient
                .builder()
                .baseUrl("http://localhost")
                .build();

        //WebClient.UriSpec<WebClient.RequestBodySpec> request = webClient.method(HttpMethod.GET);
        WebClient.RequestBodySpec getReq = webClient.method(HttpMethod.GET)
                .uri(bankUrl);

        //WebClient.ResponseSpec response =
                return getReq
                .retrieve()
                .bodyToMono(Quotation.class);
    }

}
