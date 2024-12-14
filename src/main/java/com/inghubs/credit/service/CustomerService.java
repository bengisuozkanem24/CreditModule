package com.inghubs.credit.service;


import com.inghubs.credit.request.CustomerRequest;
import com.inghubs.credit.response.DtoCustomer;

public interface CustomerService {

    DtoCustomer saveCustomer(CustomerRequest customerRequest);
}
