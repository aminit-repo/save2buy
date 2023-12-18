package com.frontlinehomes.save2buy.service.investorLand;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.repository.InvestorLandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestorLandService {
    @Autowired
    private InvestorLandRepository investorLandRepository;


    public InvestorLand addInvestorLand(InvestorLand investorLand){
         return investorLandRepository.save(investorLand);
    }

}
