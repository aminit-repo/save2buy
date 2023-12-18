package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    //this is optional, lands can be given names
    private String name;
    //total size to be purchased
    private Double SizeInSqm;

    //Amount is the total amount of land for the specified size
    private Double amount;

    //duration of payment

    @ManyToOne
    private Duration duration;

    //for DEPOSIT Frequency type, it is the initial deposit. but for frequency (WEEKLY, DAILY, MONTHLY) type it for current charge
    private Double charges;
    //frequency
    @Enumerated(EnumType.STRING)
    private Frequency frequency;
   //the total size

    private Boolean showNote=false;

    private Boolean showDurationAndFrequency=true;
    private String note;

    @CurrentTimestamp
    private Timestamp creationDate;
    @OneToMany(mappedBy = "paymentPlan", cascade = CascadeType.ALL)
    private List<LandPaymentPlan> landPaymentPlans;

    @OneToMany(mappedBy = "paymentPlan", cascade = CascadeType.ALL)
    private List<InvestorLandPaymentPlan> investorLandPaymentPlan;

    public void addInvestorLandPaymentPlan(InvestorLandPaymentPlan investorLandPaymentPlan){
        if(this.investorLandPaymentPlan == null){
            this.investorLandPaymentPlan= new ArrayList<>();
        }
        investorLandPaymentPlan.setPaymentPlan(this);
        this.investorLandPaymentPlan.add(investorLandPaymentPlan);
    }


    public void removeInvestorLandPaymentPlan(InvestorLandPaymentPlan investorLandPaymentPlan){
        investorLandPaymentPlan.setPaymentPlan(null);
        this.investorLandPaymentPlan.remove(investorLandPaymentPlan);
    }


    public void addLandPaymentPlan(LandPaymentPlan landPaymentPlan){
        if(this.landPaymentPlans == null){
            this.landPaymentPlans= new ArrayList<>();
        }
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
        return amount.equals(that.amount) && frequency.equals(that.frequency) && duration.equals(that.duration) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, frequency, duration);
    }


}
