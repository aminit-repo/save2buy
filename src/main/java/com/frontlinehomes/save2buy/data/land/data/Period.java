package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Period implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @OneToMany(mappedBy = "period", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<LandCalculatorConfigPeriod> landCalculatorConfigPeriod;

    public void addLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){

        if(this.landCalculatorConfigPeriod == null){
            this.landCalculatorConfigPeriod = new HashSet<LandCalculatorConfigPeriod>();
        }

        landCalculatorConfigPeriod.setPeriod(this);
        this.landCalculatorConfigPeriod.add(landCalculatorConfigPeriod);
    }

    public   void removeLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){
        this.landCalculatorConfigPeriod.remove(landCalculatorConfigPeriod);
        landCalculatorConfigPeriod.setPeriod(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return frequency == period.frequency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency);
    }
}
