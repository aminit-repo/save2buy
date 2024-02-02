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
import java.util.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Land implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private  Long id;

    @Column(nullable = false)
    private String title;

    private String subTitle;

    private Double priceInSqm; //this field signifies price per Sqm.

    private Double availableSize;

    private Double size;

    private String location;
    private String neigborhood;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "land")
    @Setter(AccessLevel.NONE)
    private LandCalculatorConfig calculatorConfig;
    private String img1Url;
    private String img2Url;
    @Convert(converter = YesNoConverter.class)
    private Boolean isArchived=false;
    @Convert(converter = YesNoConverter.class)
    private Boolean isSoldOut= false;
    @CurrentTimestamp
    private Timestamp createdDate;
    @Column(length = 800)
    private String description;

    private String longitude;
    private String latitude;

    @OneToMany(mappedBy = "land", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LandPaymentPlan> landPaymentPlans;

    @OneToMany(mappedBy = "land", cascade = {CascadeType.PERSIST,  CascadeType.REMOVE}, orphanRemoval = true)
    private List<InvestorLand> investorLands;

    public void addInvestorLand(InvestorLand investorLand){
        if(this.investorLands== null){
            this.investorLands= new ArrayList<>();
        }
        this.investorLands.add(investorLand);
        investorLand.setLand(this);
    }

    public void removeInvestorLand(InvestorLand investorLand){
        this.investorLands.remove(investorLand);
        investorLand.setLand(null);
    }

    public void addCalculatorConfig(LandCalculatorConfig calculatorConfig){
        this.calculatorConfig= calculatorConfig;
        calculatorConfig.setLand(this);
    }

    private void removeCalculatorConfig(LandCalculatorConfig landCalculatorConfig){
        landCalculatorConfig.setLand(null);
        this.calculatorConfig= null;
    }


    public  void addLandPaymentPlan(LandPaymentPlan landPaymentPlan){
        if(this.landPaymentPlans == null){
            this.landPaymentPlans= new HashSet<>();
        }
        landPaymentPlan.setLand(this);
        this.landPaymentPlans.add(landPaymentPlan);
    }

    public void removeLandPaymentPlan(LandPaymentPlan landPaymentPlan){
        this.landPaymentPlans.remove(landPaymentPlan);
        landPaymentPlan.setLand(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Land land = (Land) o;
        return title.equals(land.title) && priceInSqm.equals(land.priceInSqm) && size.equals(land.size) && location.equals(land.location) && neigborhood.equals(land.neigborhood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, priceInSqm, size, location, neigborhood);
    }
}
