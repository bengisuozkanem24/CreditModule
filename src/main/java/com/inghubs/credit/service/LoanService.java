package com.inghubs.credit.service;


import com.inghubs.credit.model.Loan;
import com.inghubs.credit.model.LoanInstallment;
import com.inghubs.credit.request.LoanRequest;
import com.inghubs.credit.request.PaymentRequest;
import com.inghubs.credit.response.DtoLoan;
import com.inghubs.credit.response.DtoLoanInstallment;
import com.inghubs.credit.response.PaymentResponse;

import java.util.List;

public interface LoanService {

    DtoLoan createLoan(LoanRequest loanRequest);

    List<DtoLoan> listLoans(Long customerId);

    List<DtoLoanInstallment> listLoanInstallments(Long loanId);

    PaymentResponse payLoan(PaymentRequest paymentRequest);
}
