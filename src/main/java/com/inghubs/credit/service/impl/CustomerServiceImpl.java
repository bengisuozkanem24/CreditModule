package com.inghubs.credit.service.impl;

import com.inghubs.credit.model.Customer;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.request.CustomerRequest;
import com.inghubs.credit.response.DtoCustomer;
import com.inghubs.credit.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public DtoCustomer saveCustomer(CustomerRequest customerRequest) {
        if (customerRequest.getCreditLimit() == null) {
            throw new IllegalArgumentException("Credit limit of customer cannot be null");
        }

        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setSurname(customerRequest.getSurname());
        customer.setCreditLimit(customerRequest.getCreditLimit());
        customer.setUsedCreditLimit(customerRequest.getUsedCreditLimit());

        customerRepository.save(customer);

        return createDtoCustomer(customer);
    }

    private DtoCustomer createDtoCustomer(Customer customer) {
        DtoCustomer dtoCustomer = new DtoCustomer();
        dtoCustomer.setId(customer.getId());
        dtoCustomer.setName(customer.getName());
        dtoCustomer.setSurname(customer.getSurname());
        dtoCustomer.setCreditLimit(customer.getCreditLimit());
        dtoCustomer.setUsedCreditLimit(customer.getUsedCreditLimit());
        return dtoCustomer;
    }
}
