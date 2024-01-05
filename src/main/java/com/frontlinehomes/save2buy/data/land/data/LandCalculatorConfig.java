package com.frontlinehomes.save2buy.data.land.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class LandCalculatorConfig implements Serializable {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "landCalculatorConfig", cascade = {CascadeType.ALL})
    private Set<LandCalculatorConfigPeriod> frequencies;
    @OneToMany(mappedBy = "landCalculatorConfig", cascade = {CascadeType.ALL})
    private Set<LandCalculatorConfigDuration> durationList;

    private Double maxLandSize;

    private Double minLandSize;

    @OneToOne
    private Land land;

    public void addLandCalculatorConfigDuration(LandCalculatorConfigDuration landCalculatorConfigDuration){
        if(this.durationList == null){
            this.durationList= new HashSet<>();
        }
        landCalculatorConfigDuration.setLandCalculatorConfig(this);
        this.durationList.add(landCalculatorConfigDuration);
    }

    public void addLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){
        if(this.frequencies == null){
            this.frequencies= new HashSet<>();
        }
        landCalculatorConfigPeriod.setLandCalculatorConfig(this);
        this.frequencies.add(landCalculatorConfigPeriod);
    }

    public void removeLandCalculatorConfigDuration(LandCalculatorConfigDuration landCalculatorConfigDuration){
        this.durationList.remove(landCalculatorConfigDuration);
        landCalculatorConfigDuration.setLandCalculatorConfig(null);
    }

    public void removeLandCalculatorConfigPeriod(LandCalculatorConfigPeriod landCalculatorConfigPeriod){
        this.frequencies.remove(landCalculatorConfigPeriod);
        landCalculatorConfigPeriod.setLandCalculatorConfig(null);
    }

}
