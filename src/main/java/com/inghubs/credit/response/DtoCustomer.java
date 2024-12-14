package com.inghubs.credit.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCustomer {

    private Long id;
    private String name;
    private String surname;
    private Double creditLimit;
    private Double usedCreditLimit;
}
