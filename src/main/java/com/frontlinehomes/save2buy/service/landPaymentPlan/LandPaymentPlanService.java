package com.frontlinehomes.save2buy.service.landPaymentPlan;

import com.frontlinehomes.save2buy.data.land.data.Land;
import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.data.PaymentPlan;
import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import com.frontlinehomes.save2buy.repository.LandPaymentPlanRepository;
import com.frontlinehomes.save2buy.repository.PaymentPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LandPaymentPlanService {

    @Autowired
    private PaymentPlanRepository paymentPlanRepository;

    @Autowired
    private LandPaymentPlanRepository landPaymentPlanRepository;



    public PaymentPlan getPaymentPlanById(Long id){
        try {
          Optional<PaymentPlan> paymentPlan= paymentPlanRepository.findById(id);
          return  paymentPlan.get();
        }catch (NoSuchElementException e){
            return  null;
        }

    }



    public LandPaymentPlan getLandPaymentPlanById(Long id){
        try {
            Optional<LandPaymentPlan> landPaymentPlan= landPaymentPlanRepository.findById(id);
            return  landPaymentPlan.get();
        }catch (NoSuchElementException e){
            return  null;
        }
    }



    public PaymentPlan savePaymentPlan(PaymentPlan paymentPlan){
          return  paymentPlanRepository.save(paymentPlan);
    }

    public LandPaymentPlan saveLandPaymentPlan(LandPaymentPlan landPaymentPlan){
            return  landPaymentPlanRepository.save(landPaymentPlan);
    }





}
