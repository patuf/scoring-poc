package com.example.demo.dumpster;

import com.example.demo.bo.LoanContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface LoanContractRepository extends CrudRepository<LoanContract, Long> {

    Page<LoanContract> findAll(Pageable pageable);

    LoanContract findById(String id);
    LoanContract findByCustomerId(String custId);

}