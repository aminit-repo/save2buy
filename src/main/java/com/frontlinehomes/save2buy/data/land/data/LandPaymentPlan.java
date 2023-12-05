package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class LandPaymentPlan implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private Land land;
    @ManyToOne
    private PaymentPlan paymentPlan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandPaymentPlan that = (LandPaymentPlan) o;
        return land.equals(that.land) && paymentPlan.equals(that.paymentPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(land, paymentPlan);
    }
}
