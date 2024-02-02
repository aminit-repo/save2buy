package com.frontlinehomes.save2buy.data.land.data;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.account.data.Transaction;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InvestorLand implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private  Long id;
    @ManyToOne
    private Land land;
    @ManyToOne
    private Investor investor;

    private Double size;

    private Double amount;

    private String longitude;
    private String latitude;
    private Integer milestone;
    @Enumerated(EnumType.STRING)
    private LandStatus landStatus;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    @CurrentTimestamp
    private Timestamp creationDate;


    @OneToMany(mappedBy = "investorLand", cascade = {CascadeType.ALL})
    @Setter(AccessLevel.NONE)
    private List<InvestorLandPaymentPlan> investorLandPaymentPlan;


    @OneToMany(mappedBy = "investorLand", cascade = {CascadeType.PERSIST,  CascadeType.REMOVE}, orphanRemoval = true)
    private List<Transaction> transactionList;

    public void addTransaction(Transaction transaction){
        if(this.transactionList== null){
            this.transactionList= new ArrayList<>();
        }
        transaction.setInvestorLand(this);
        this.transactionList.add(transaction);
    }

    public void removeTransaction(Transaction transaction){
        this.transactionList.remove(transaction);
        transaction.setInvestorLand(null);
    }

    public void addInvestorLandPaymentPlan(InvestorLandPaymentPlan investorLandPaymentPlan){
        if(this.investorLandPaymentPlan== null){
            this.investorLandPaymentPlan= new ArrayList<>();
        }
        investorLandPaymentPlan.setInvestorLand(this);
        this.investorLandPaymentPlan.add(investorLandPaymentPlan);
    }

    public  void removeInvestorLandPaymentPlan(InvestorLandPaymentPlan investorLandPaymentPlan){
        this.investorLandPaymentPlan.remove(investorLandPaymentPlan);
        investorLandPaymentPlan.setInvestorLand(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestorLand that = (InvestorLand) o;
        return land.equals(that.land) && investor.equals(that.investor) && size.equals(that.size) && amount.equals(that.amount) && investorLandPaymentPlan.equals(that.investorLandPaymentPlan);
    }


    @Override
    public int hashCode() {
        return Objects.hash(land, investor, size, amount, investorLandPaymentPlan);
    }
}
