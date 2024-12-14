package com.inghubs.credit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long loanId;
    private Double amount;
}
