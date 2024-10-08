package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.land.data.LandPaymentPlan;

import java.sql.Timestamp;
import java.util.Set;

public class UpdateLandDTO {
    private String title;
    private String subTitle;
    private Double priceInSqm;
    private Double availableSize;
    private Double size;
    private String location;
    private String neigborhood;
    private Boolean isSoldOut;
    private String description;

    private String img1Url;
    private String img2Url;

    private String longitude;
    private String latitude;

}
