package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class PaymentPlan implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    private String name;

    private Double amount;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @CurrentTimestamp
    private Timestamp creationDate;
    @OneToMany(mappedBy = "paymentPlan", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private List<LandPaymentPlan> landPaymentPlans;
    @OneToOne
    private InvestorLand investorLand;

    public void addLandPaymentPlan(LandPaymentPlan landPaymentPlan){
        landPaymentPlan.setPaymentPlan(this);
        this.landPaymentPlans.add(landPaymentPlan);
    }

    public void removeLandPaymentPlan(LandPaymentPlan landPaymentPlan){
        this.landPaymentPlans.remove(landPaymentPlan);
        landPaymentPlan.setPaymentPlan(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentPlan that = (PaymentPlan) o;
        return amount.equals(that.amount) && frequency.equals(that.frequency) && duration == that.duration ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, frequency, duration);
    }

}
