package com.frontlinehomes.save2buy.service.transaction;

import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  java.util.*;

import java.util.NoSuchElementException;

@Service
@Transactional
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionByRefNumber(String refNumber) throws NoSuchElementException{
        try{
            Transaction transaction= transactionRepository.findByRefNumber(refNumber);
            if(transaction == null) throw  new NoSuchElementException("Transaction with refNumber "+refNumber+" not found");
            return transaction;
        }catch (NoSuchElementException e){
            throw  new NoSuchElementException("Transaction with refNumber "+refNumber+" not found");
        }
    }

    public List<Transaction> getAllTransactionByInvestorLand(InvestorLand investorLand){
        return transactionRepository.findAllByInvestorLand(investorLand);
    }

}
