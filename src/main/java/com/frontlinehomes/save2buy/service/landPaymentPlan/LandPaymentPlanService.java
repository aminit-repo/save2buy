package com.frontlinehomes.save2buy.service.landPaymentPlan;

import com.frontlinehomes.save2buy.data.land.data.*;
import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import com.frontlinehomes.save2buy.exception.CalculatorConfigException;
import com.frontlinehomes.save2buy.exception.NotNullFieldException;
import com.frontlinehomes.save2buy.repository.InvestorLandPaymentPlanRepository;
import com.frontlinehomes.save2buy.repository.LandPaymentPlanRepository;
import com.frontlinehomes.save2buy.repository.PaymentPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.*;

@Service
@Transactional
public class LandPaymentPlanService {

    @Autowired
    private PaymentPlanRepository paymentPlanRepository;

    @Autowired
    private LandPaymentPlanRepository landPaymentPlanRepository;

    @Autowired
    private InvestorLandPaymentPlanRepository investorLandPaymentPlanRepository;



    public PaymentPlan getPaymentPlanById(Long id) throws NoSuchElementException{
        try {
          Optional<PaymentPlan> paymentPlan= paymentPlanRepository.findById(id);
          if(paymentPlan.get() == null) throw new NoSuchElementException("PaymentPlan with id "+id+" cannot be found");
          return  paymentPlan.get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("PaymentPlan with id "+id+" cannot be found");
        }
    }



    public LandPaymentPlan getLandPaymentPlanById(Long id) throws NoSuchElementException{
        try {
            Optional<LandPaymentPlan> landPaymentPlan= landPaymentPlanRepository.findById(id);
            if(landPaymentPlan.get() == null) throw new NoSuchElementException("landPayent plan with Id "+id+"cannot be found");
            return  landPaymentPlan.get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("landPayment plan with Id "+id+" cannot be found");
        }
    }



    public PaymentPlan savePaymentPlan(PaymentPlan paymentPlan){
          return  paymentPlanRepository.save(paymentPlan);
    }

    public LandPaymentPlan saveLandPaymentPlan(LandPaymentPlan landPaymentPlan){
            return  landPaymentPlanRepository.save(landPaymentPlan);
    }

    public InvestorLandPaymentPlan addInvestorLandPaymentPlan(InvestorLandPaymentPlan investorLandPaymentPlan){
        return  investorLandPaymentPlanRepository.save(investorLandPaymentPlan);
    }

    public void removeAllInvestorPaymentPlans(InvestorLand investorLand){
        investorLandPaymentPlanRepository.deleteAllByInvestorLand(investorLand);
    }

    /**
     *
     *
     * @param paymentPlan
     * @return
     *
     * validates the payment plan rules, to ensure payment plans are submitted in right format
     *  if duration Type is OneOff, Frequency should not be specified
     *
     *  sizeInSqm must be provided
     *
     *
     */




}










