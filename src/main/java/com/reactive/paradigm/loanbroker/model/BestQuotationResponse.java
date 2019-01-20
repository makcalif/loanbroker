package com.reactive.paradigm.loanbroker.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestQuotationResponse that = (BestQuotationResponse) o;
        return   Objects.equals(requestedLoanAmount, that.requestedLoanAmount) &&
                //Objects.equals(offers, that.offers) &&
                Objects.equals(bestOffer, that.bestOffer);
    }

}
