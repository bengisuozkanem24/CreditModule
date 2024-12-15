package com.inghubs.credit.service.impl;


import com.inghubs.credit.model.Customer;
import com.inghubs.credit.model.Loan;
import com.inghubs.credit.model.LoanInstallment;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.request.LoanRequest;
import com.inghubs.credit.request.PaymentRequest;
import com.inghubs.credit.response.DtoLoan;
import com.inghubs.credit.response.DtoLoanInstallment;
import com.inghubs.credit.response.PaymentResponse;
import com.inghubs.credit.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Override
    public DtoLoan createLoan(LoanRequest loanRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findById(loanRequest.getCustomerId());
        if (optionalCustomer.isEmpty()) {
            throw new IllegalArgumentException("Customer not found.");
        }

        Customer customer = optionalCustomer.get();
        Double totalLoanAmount = loanRequest.getAmount() * (1 + loanRequest.getInterestRate());

        validate(customer, loanRequest, totalLoanAmount);

        Loan loan = createLoan(loanRequest, customer, totalLoanAmount);

        createInstallment(loan, totalLoanAmount, loanRequest);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + totalLoanAmount);
        customerRepository.save(customer);

        return createDtoLoan(loan);
    }

    private void validate(Customer customer, LoanRequest loanRequest, Double totalLoanAmount) {
        if (customer.getUsedCreditLimit() + totalLoanAmount > customer.getCreditLimit()) {
            throw new IllegalArgumentException("Customer has insufficient credit limit to get this new loan");
        }

        List<Integer> numberOfInstallments = List.of(6, 9, 12, 24);
        if (!numberOfInstallments.contains(loanRequest.getNumberOfInstallments())) {
            throw new IllegalArgumentException("Number of installments can only be 6, 9, 12, 24");
        }

        if (loanRequest.getInterestRate() < 0.1 || loanRequest.getInterestRate() > 0.5) {
            throw new IllegalArgumentException("Interest rate must be between 0.1 – 0.5");
        }
    }

    private Loan createLoan(LoanRequest loanRequest, Customer customer, Double totalLoanAmount) {
        Loan loan = new Loan();
        loan.setCustomerId(customer.getId());
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallment(loanRequest.getNumberOfInstallments());
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);
        loan = loanRepository.save(loan);
        return loan;
    }

    private void createInstallment(Loan loan, Double totalLoanAmount, LoanRequest loanRequest) {
        Double installmentAmount = totalLoanAmount / loanRequest.getNumberOfInstallments();
        for (int i = 1; i <= loanRequest.getNumberOfInstallments(); i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoanId(loan.getId());
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(0.0);
            installment.setDueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1));
            installment.setIsPaid(false);
            loanInstallmentRepository.save(installment);
        }
    }

    private DtoLoan createDtoLoan(Loan loan) {
        DtoLoan dtoLoan = new DtoLoan();
        dtoLoan.setId(loan.getId());
        dtoLoan.setCustomerId(loan.getCustomerId());
        dtoLoan.setLoanAmount(loan.getLoanAmount());
        dtoLoan.setNumberOfInstallment(loan.getNumberOfInstallment());
        dtoLoan.setCreateDate(loan.getCreateDate());
        dtoLoan.setIsPaid(loan.getIsPaid());
        List<LoanInstallment> installments = loan.getInstallments();
        if (installments == null || installments.isEmpty()) {
            installments = loanInstallmentRepository.findByLoanId(loan.getId());
        }
        List<DtoLoanInstallment> dtoInstallments = installments.stream()
                .map(this::createDtoLoanInstallments)
                .collect(Collectors.toList());
        dtoLoan.setInstallments(dtoInstallments);
        return dtoLoan;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DtoLoan> listLoans(Long customerId) {
        List<Loan> loans = loanRepository.findByCustomerId(customerId);
        return loans != null ? loans.stream()
                .map(this::createDtoLoan)
                .collect(Collectors.toList()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DtoLoanInstallment> listLoanInstallments(Long loanId) {
        List<LoanInstallment> loanInstallments = loanInstallmentRepository.findByLoanId(loanId);
        return loanInstallments != null ? loanInstallments.stream()
                .map(this::createDtoLoanInstallments)
                .collect(Collectors.toList()) : null;
    }

    private DtoLoanInstallment createDtoLoanInstallments(LoanInstallment loanInstallment) {
        DtoLoanInstallment dtoLoanInstallment = new DtoLoanInstallment();
        dtoLoanInstallment.setId(loanInstallment.getId());
        dtoLoanInstallment.setLoanId(loanInstallment.getLoanId());
        dtoLoanInstallment.setAmount(loanInstallment.getAmount());
        dtoLoanInstallment.setIsPaid(loanInstallment.getIsPaid());
        dtoLoanInstallment.setPaidAmount(loanInstallment.getPaidAmount());
        dtoLoanInstallment.setDueDate(loanInstallment.getDueDate());
        dtoLoanInstallment.setPaymentDate(loanInstallment.getPaymentDate());
        return dtoLoanInstallment;
    }

    @Override
    public PaymentResponse payLoan(PaymentRequest paymentRequest) {
        Optional<Loan> optionalLoan = loanRepository.findById(paymentRequest.getLoanId());
        if (optionalLoan.isEmpty()) {
            throw new IllegalArgumentException("Loan not found.");
        }

        Loan loan = optionalLoan.get();
        if (loan.getIsPaid()) {
            throw new IllegalArgumentException("Loan is paid.");
        }

        List<LoanInstallment> loanInstallments = loanInstallmentRepository.findByLoanId(paymentRequest.getLoanId());
        loanInstallments.sort(Comparator.comparing(LoanInstallment::getDueDate));

        double remainingAmount = paymentRequest.getAmount();
        int numberOfPaidInstallments = 0;

        for (LoanInstallment loanInstallment : loanInstallments) {
            if(paymentRequest.getAmount() < loanInstallment.getAmount()) {
                throw new IllegalArgumentException("Amount is not enough to pay installment.");
            }

            if (remainingAmount < loanInstallment.getAmount() || loanInstallment.getIsPaid() || loanInstallment.getDueDate().isAfter(LocalDate.now().plusMonths(3))) {
                continue;
            }

            double change = 0.0;
            long dayDifference = java.time.temporal.ChronoUnit.DAYS.between(loanInstallment.getDueDate(), LocalDate.now());

            if (dayDifference < 0) {
                change = -0.001 * Math.abs(dayDifference) * loanInstallment.getAmount();
            } else if (dayDifference > 0) {
                change = 0.001 * dayDifference * loanInstallment.getAmount();
            }

            double changedAmount = loanInstallment.getAmount() + change;

            remainingAmount -= changedAmount;
            loanInstallment.setPaidAmount(loanInstallment.getAmount());
            loanInstallment.setPaymentDate(LocalDate.now());
            loanInstallment.setIsPaid(true);
            loanInstallmentRepository.save(loanInstallment);

            numberOfPaidInstallments++;
        }

        double totalPaid = loanInstallments.stream().limit(numberOfPaidInstallments).mapToDouble(LoanInstallment::getAmount).sum();
        PaymentResponse paymentResponse = new PaymentResponse(numberOfPaidInstallments, totalPaid, checkLoanIsPaid(loanInstallments,loan));

        return paymentResponse;
    }

    private boolean checkLoanIsPaid(List<LoanInstallment> loanInstallments, Loan loan) {
        boolean isLoanPaid = loanInstallments.stream().allMatch(LoanInstallment::getIsPaid);
        if (isLoanPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }
        return isLoanPaid;
    }
}
