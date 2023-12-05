package com.frontlinehomes.save2buy.data.users.investor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frontlinehomes.save2buy.data.account.data.Account;
import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import com.frontlinehomes.save2buy.data.users.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity
@NoArgsConstructor
@Setter
@Getter
public class Investor  implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    @JsonIgnore
    @OneToOne
    private User user;
    @OneToOne(mappedBy = "investor", cascade = {CascadeType.PERSIST, CascadeType.DETACH}, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Account account;
    private String address;
    private String nationality;
    private String occupation;
    private String employeeName;
    private String employeeAddress;
    private String sourceOfIncome;
    private String passportUrl;
    private String idCardUrl;
    private String monthlyIncomeEstimate;
    private String nextOfKinName;
    private  String nextOfKinPhone;
    private String nextOfKinAddress;
    private String nextOfKinRelationship;
    @OneToMany(mappedBy = "investor", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<InvestorLand> investorLands;

    public void addAccount(Account account){
        this.account= account;
        account.setInvestor(this);
    }

    public void addInvestorLands(InvestorLand investorLand){
        investorLand.setInvestor(this);
        investorLands.add(investorLand);
    }

    public void removeInvestorLands(InvestorLand investorLand){
        this.investorLands.remove(investorLand);
        investorLand.setInvestor(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investor investor = (Investor) o;
        return user.equals(investor.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
