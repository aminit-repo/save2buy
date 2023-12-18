package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class InvestorLandPaymentPlan implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne
    private InvestorLand investorLand;
    @ManyToOne
    private PaymentPlan paymentPlan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestorLandPaymentPlan that = (InvestorLandPaymentPlan) o;
        return investorLand.equals(that.investorLand) && paymentPlan.equals(that.paymentPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(investorLand, paymentPlan);
    }
}
