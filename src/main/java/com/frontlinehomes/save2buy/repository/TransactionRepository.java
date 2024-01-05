package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.account.data.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Transaction findByRefNumber(String refNumber);
}
