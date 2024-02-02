package com.frontlinehomes.save2buy.service.investorLand;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.repository.InvestorLandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.*;

@Service
@Transactional
public class InvestorLandService {
    @Autowired
    private InvestorLandRepository investorLandRepository;


    public InvestorLand addInvestorLand(InvestorLand investorLand){
         return investorLandRepository.save(investorLand);
    }

    public InvestorLand getInvestorLand(Long id) throws NoSuchElementException {
        try{
            Optional<InvestorLand> investorLand= investorLandRepository.findById(id);

            if(investorLand.isEmpty()) throw new NoSuchElementException("Investor's Land with id= "+id+"cannot be found");

            return investorLand.get();
        }catch (Exception e){
            throw new NoSuchElementException("Investor's Land with id= "+id+"cannot be found");
        }
    }

    public InvestorLand getInvestorLandByLand(Investor investor,Land land){
        try {
            InvestorLand investorLand= investorLandRepository.findByInvestorAndLand(investor, land);
            if(investorLand == null)  throw new NoSuchElementException("Investor Land cannot be found");
            return investorLand;
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Investor Land cannot be found");
        }
    }

    public InvestorLand getInvestorLandById(Long id){
        try{
            InvestorLand investorLand= investorLandRepository.findById(id).get();
            if(investorLand== null)  throw new NoSuchElementException("Investor Land cannot be found");

            return investorLand;
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Investor Land cannot be found");
        }
    }

    public List<InvestorLand> getAllInitiatedInvestorLand(){
        return investorLandRepository.findAllByLandStatus(LandStatus.Initiated);
    }

    public List<InvestorLand> getAllInitiatedInvestorLandByID(Long id){
        return investorLandRepository.findAllByIdAndLandStatus(id,LandStatus.Initiated);
    }




}
