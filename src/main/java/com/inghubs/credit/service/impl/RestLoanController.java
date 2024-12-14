package com.inghubs.credit.service.impl;

import com.inghubs.credit.model.Customer;
import com.inghubs.credit.model.Loan;
import com.inghubs.credit.model.LoanInstallment;
import com.inghubs.credit.request.CustomerRequest;
import com.inghubs.credit.request.LoanRequest;
import com.inghubs.credit.request.PaymentRequest;
import com.inghubs.credit.response.DtoCustomer;
import com.inghubs.credit.response.DtoLoan;
import com.inghubs.credit.response.DtoLoanInstallment;
import com.inghubs.credit.response.PaymentResponse;
import com.inghubs.credit.service.CustomerService;
import com.inghubs.credit.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class RestLoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<DtoLoan> createLoan(@RequestBody LoanRequest loanRequest) {
        return ResponseEntity.ok(loanService.createLoan(loanRequest));
    }

    @GetMapping("/list/{customerId}")
    public ResponseEntity<List<DtoLoan>> listLoans(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.listLoans(customerId));
    }

    @GetMapping("/installments/{loanId}")
    public ResponseEntity<List<DtoLoanInstallment>> listInstallments(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.listLoanInstallments(loanId));
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> payLoan(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(loanService.payLoan(paymentRequest));
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<DtoCustomer> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.saveCustomer(customerRequest));
    }
}
