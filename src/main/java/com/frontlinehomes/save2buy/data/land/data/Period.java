package com.frontlinehomes.save2buy.data.land.data;

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
public class Period  implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    private Frequency frequency;

    private Integer weight;

    @OneToMany(mappedBy = "period", cascade = {CascadeType.ALL})
    private Set<LandCalculatorConfigPeriod> landCalculatorConfigPeriod= new HashSet<>();;

    public void addLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){
        landCalculatorConfigPeriod.setPeriod(this);
        this.landCalculatorConfigPeriod.add(landCalculatorConfigPeriod);
    }

    public void removeLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){
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
