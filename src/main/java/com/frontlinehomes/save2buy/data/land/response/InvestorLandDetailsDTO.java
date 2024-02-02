package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.data.land.data.Frequency;
import com.frontlinehomes.save2buy.data.land.data.LandStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class InvestorLandDetailsDTO {
    private Long id;
    private String title;
    private String investorFirstName;
    private String investorLastName;
    private Double amount;
    private Double size;
    private String investorOtherName;
    private String latitude;
    private String longitude;
    private String location;
    private String neigborhood;
    private Double charges;
    private Integer durationLength;
    private DurationType durationType;
    private Frequency frequency;
    private Timestamp creationDate;
    private LandStatus landStatus;
    private Integer milestone;

}
