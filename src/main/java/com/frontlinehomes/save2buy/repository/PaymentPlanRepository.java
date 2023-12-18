package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.data.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {

}
