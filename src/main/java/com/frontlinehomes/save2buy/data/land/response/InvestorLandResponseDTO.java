package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
public class InvestorLandResponseDTO {
    private Long id;
    private Long userId;
    private Long landId;

    private Double size;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    private Timestamp creationDate;

    private Long paymentPlanId;

}
