package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.land.data.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class CalculatorConfigDTO {
    private ArrayList<DurationDTO> durationList;

    private ArrayList<PeriodDTO> periodList;

    private Double maxLandSize;

    private Double minLandSize;

    private Long landId;

}
