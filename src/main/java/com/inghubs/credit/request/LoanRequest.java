package com.inghubs.credit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {

    private Long customerId;
    private Double amount;
    private Double interestRate;
    private Integer numberOfInstallments;
}
