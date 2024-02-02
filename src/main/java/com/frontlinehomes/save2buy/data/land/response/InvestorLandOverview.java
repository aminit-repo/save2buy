package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.sql.Timestamp;
import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvestorLandOverview {
    private Long id;
    private String title;
    private Double amount;
    private Double paid;
    private Double balance;
    private List payRate;
    private Integer milestone;

}
