package com.frontlinehomes.save2buy.data.account.request;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InitTransactionRequestDTO {

    /** purchaseId is same as investorLand's id  **/
    private Long purchaseId;

    @Enumerated(EnumType.STRING)
    private BillingType paymentMethod;

    private Long paymentPlanId;


}
