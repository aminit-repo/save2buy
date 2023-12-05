package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Setter
@Getter
public class LandDetailsDTO {
    private  Long id;
    private String title;
    private String subTitle;
    private Double priceInSqm;
    private Double availableSize;
    private Double size;
    private String location;
    private String neigborhood;
    private Timestamp createdDate;
    private Boolean isSoldOut;

    private Boolean isArchived;
    private String description;
    private String img1Url;
    private String img2Url;
    private String longitude;
    private String latitude;
    private Set<LandPaymentPlan> landPaymentPlans;

}
