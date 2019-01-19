package com.reactive.paradigm.loanbroker.model;

import java.util.ArrayList;
import java.util.List;

public class BestQuotationResponse {
    private long timestamp = System.currentTimeMillis();
    private Double requestedLoanAmount = 0d;
    private long duration;
    private List<Quotation> offers = new ArrayList<Quotation>(9);
    private Quotation bestOffer;

    public BestQuotationResponse(){}

    public BestQuotationResponse(Double requestedLoanAmount){
        this.requestedLoanAmount = requestedLoanAmount;
    }

    public Double getRequestedLoanAmount() {
        return this.requestedLoanAmount;
    }

    public void bestOffer(Quotation quotation) {
        bestOffer = quotation;
    }

    public Quotation getBestOffer(){
        return bestOffer;
    }

    public void offer(Quotation quotation) {
        offers.add(quotation);
    }

    public long getDuration() {
        return this.duration;
    }

    public int getTotalOffers() {
        return offers.size();
    }

    public void finish() {
        this.duration = System.currentTimeMillis() - this.timestamp;
    }

    public List<Quotation> getOffers(){
        return offers;
    }

    @Override
    public String toString() {
        return requestedLoanAmount +" : "+ getBestOffer().toString();
    }
}
