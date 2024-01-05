package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.land.data.InvestorLandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorLandPaymentPlanRepository extends JpaRepository<InvestorLandPaymentPlan, Long> {
    public void deleteAllByInvestorLand(InvestorLand investorLand);

}
