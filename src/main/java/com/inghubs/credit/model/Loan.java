package com.inghubs.credit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "LOAN")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private Double loanAmount;
    private Integer numberOfInstallment;
    private LocalDate createDate;
    private Boolean isPaid;
    @OneToMany(mappedBy = "loanId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<LoanInstallment> installments;



}
