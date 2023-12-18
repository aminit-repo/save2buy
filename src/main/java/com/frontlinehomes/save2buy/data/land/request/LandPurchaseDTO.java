package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;
import com.frontlinehomes.save2buy.data.land.response.PaymentPlanResponseDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * PaymentPlanId and paymentPlan cannot be set at together,
 * if paymentPlanId is set, paymentPlan should be null
 * */
@Getter
@Setter
public class LandPurchaseDTO {
    private  Long userId;
    @Enumerated(EnumType.STRING)
    private BillingType billingType;
    private Double size;
    private Long paymentPlanId;
    private PaymentPlanDTO paymentPlan;

}
