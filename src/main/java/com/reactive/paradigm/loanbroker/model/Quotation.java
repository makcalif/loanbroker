package com.reactive.paradigm.loanbroker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quotation {
    String bank;
    Double offer;


}
