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
    /** purchaseId is same as investorLand's id  **/
    private Long purchaseId;
    private  Long userId;

    private String email;

    private Long paymentPlanId;
    private LandPurchasePaymentPlanDTO paymentPlan;

}
