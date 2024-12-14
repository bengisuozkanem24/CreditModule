package com.inghubs.credit;


import com.inghubs.credit.model.Customer;
import com.inghubs.credit.model.Loan;
import com.inghubs.credit.model.LoanInstallment;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.request.LoanRequest;
import com.inghubs.credit.request.PaymentRequest;
import com.inghubs.credit.response.DtoLoan;
import com.inghubs.credit.response.PaymentResponse;
import com.inghubs.credit.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CreditApplicationTests {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_returnLoan_when_createLoan() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(0.0);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomerId(1L);
        loanRequest.setAmount(100.0);
        loanRequest.setInterestRate(0.2);
        loanRequest.setNumberOfInstallments(6);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DtoLoan dtoLoan = loanService.createLoan(loanRequest);

        assertEquals(1L, dtoLoan.getCustomerId());
        assertEquals(120.0, dtoLoan.getLoanAmount());
        assertEquals(6, dtoLoan.getNumberOfInstallment());
    }

    @Test
    public void should_returnInsufficientCredit_when_createLoan() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(2000.0);
        customer.setUsedCreditLimit(1000.0);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomerId(1L);
        loanRequest.setAmount(1000.0);
        loanRequest.setInterestRate(0.2);
        loanRequest.setNumberOfInstallments(6);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(loanRequest));
        assertEquals("Customer has insufficient credit limit to get this new loan", exception.getMessage());
    }

    @Test
    public void should_returnLoans_when_list() {
        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setCustomerId(1L);
        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setCustomerId(1L);

        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of(loan1, loan2));
        when(loanInstallmentRepository.findByLoanId(anyLong())).thenReturn(List.of(new LoanInstallment()));

        List<DtoLoan> loans = loanService.listLoans(1L);

        assertEquals(2, loans.size());
        verify(loanRepository).findByCustomerId(1L);
    }

    @Test
    public void should_returnPaymentResponse_when_PayLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setIsPaid(false);

        List<LoanInstallment> loanInstallments = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setId(Long.valueOf(i));
            installment.setLoanId(1L);
            installment.setAmount(500.0);
            installment.setPaidAmount(0.0);
            installment.setDueDate(LocalDate.now().plusMonths(i));
            installment.setIsPaid(false);
            loanInstallments.add(installment);
        }


        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(1L)).thenReturn(loanInstallments);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setLoanId(1L);
        paymentRequest.setAmount(1000.0);

        PaymentResponse response = loanService.payLoan(paymentRequest);

        assertEquals(2, response.getInstallmentsPaid());
        assertEquals(1000.0, response.getTotalPaid());
        assertFalse(response.isLoanPaid());
    }

}
