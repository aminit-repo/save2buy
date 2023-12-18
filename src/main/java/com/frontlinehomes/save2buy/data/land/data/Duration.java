package com.frontlinehomes.save2buy.data.land.data;

import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Duration  implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    private Integer length;
    @Enumerated(EnumType.STRING)
    private DurationType frequency;
    @OneToMany(mappedBy = "duration", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<PaymentPlan> paymentPlan;



    private Integer weight;

    @OneToMany(mappedBy = "duration", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<LandCalculatorConfigDuration> landCalculatorConfigDuration= new HashSet<>();


    public void addPaymentPlan(PaymentPlan paymentPlan){
        if(this.paymentPlan== null){
            this.paymentPlan= new HashSet<>();
        }
        paymentPlan.setDuration(this);
        this.paymentPlan.add(paymentPlan);
    }

    public void removePaymentPlan(PaymentPlan paymentPlan){
        this.paymentPlan.remove(paymentPlan);
        paymentPlan.setDuration(null);
    }

    public void addLandCalculatorConfigDuration(LandCalculatorConfigDuration landCalculatorConfigDuration){
        if(this.landCalculatorConfigDuration==null){
            this.landCalculatorConfigDuration= new HashSet<>();
        }
        landCalculatorConfigDuration.setDuration(this);
        this.landCalculatorConfigDuration.add(landCalculatorConfigDuration);
    }

    public void removeLandCalculatorConfigDuration(LandCalculatorConfigDuration landCalculatorConfigDuration){
        this.landCalculatorConfigDuration.remove(landCalculatorConfigDuration);
        landCalculatorConfigDuration.setDuration(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return length.equals(duration.length) && frequency.equals(duration.frequency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, frequency);
    }
}
