package com.frontlinehomes.save2buy.service;

import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.repository.InvestorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.*;

@Service
@Transactional
public class InvestorService {
    @Autowired
    InvestorRepository investorRepository;

    public Investor addInvestor(Investor investor){
        return investorRepository.save(investor);
    }

    public Investor getInvestor(Long Id){
        try{
          Optional<Investor> investor= investorRepository.findById(Id);
          return investor.get();
        }catch (NoSuchElementException e){
            throw e;
        }
    }

    public List<Investor> getAllInvestors(){
        return investorRepository.findAll(Sort.by("id").descending());
    }



}
