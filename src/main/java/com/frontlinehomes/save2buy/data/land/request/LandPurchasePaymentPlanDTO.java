package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.data.land.data.Frequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandPurchasePaymentPlanDTO {

    private Double SizeInSqm;

    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    private  Integer durationLength;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;
}
