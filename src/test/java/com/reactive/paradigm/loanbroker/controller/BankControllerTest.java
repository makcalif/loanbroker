package com.reactive.paradigm.loanbroker.controller;

import com.reactive.paradigm.loanbroker.model.BestQuotationResponse;
import com.reactive.paradigm.loanbroker.model.Quotation;
import com.sun.org.apache.xpath.internal.operations.Quo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BankControllerTest {

    BankController bankController;

    @Before
    public void before() {
        bankController = new BankController();
    }

    @Test
    public void testWebCall() {
        Mono<BestQuotationResponse> response = bankController.requestForQuotation("Bank-1", 1000D);

        BestQuotationResponse expected = new BestQuotationResponse(1000d);
        Quotation q = new Quotation("Bank-1", 1000d);
        expected.bestOffer(q);
        StepVerifier.create(response).expectNext(expected).verifyComplete();

    }

    @Test
    public void isolate() {
        Mono<BestQuotationResponse> bqr = bankController.getBestQuotation();

        BestQuotationResponse expected = new BestQuotationResponse(1000d);
        Quotation q = new Quotation("Bank-1", 1000d);
        expected.bestOffer(q);

        StepVerifier.create(bqr).expectNext(expected).verifyComplete();

    }
}