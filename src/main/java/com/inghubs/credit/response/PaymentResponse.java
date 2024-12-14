package com.inghubs.credit.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {

    private int installmentsPaid;
    private double totalPaid;
    private boolean isLoanPaid;
}
