package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class LandCalculatorConfigPeriod implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Period period;

    @ManyToOne
    private LandCalculatorConfig landCalculatorConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandCalculatorConfigPeriod that = (LandCalculatorConfigPeriod) o;
        return period.equals(that.period) && landCalculatorConfig.equals(that.landCalculatorConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, landCalculatorConfig);
    }
}
