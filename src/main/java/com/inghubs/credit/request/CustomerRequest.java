package com.inghubs.credit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    private String name;
    private String surname;
    private Double creditLimit;
    private Double usedCreditLimit;
}
