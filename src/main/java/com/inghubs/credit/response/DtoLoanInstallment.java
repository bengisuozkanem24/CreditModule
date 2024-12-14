package com.inghubs.credit.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DtoLoanInstallment {

    private Long id;
    private Long loanId;
    private Double amount;
    private Double paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Boolean isPaid;
}
