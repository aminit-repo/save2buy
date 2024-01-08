package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Transaction findByRefNumber(String refNumber);

    public List<Transaction> findAllByInvestorLand(InvestorLand investorLand);
}
