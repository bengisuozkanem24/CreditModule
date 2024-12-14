package com.inghubs.credit.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DtoLoan {

    private Long id;
    private Long customerId;
    private Double loanAmount;
    private Integer numberOfInstallment;
    private LocalDate createDate;
    private Boolean isPaid;
    private List<DtoLoanInstallment> installments;
}
