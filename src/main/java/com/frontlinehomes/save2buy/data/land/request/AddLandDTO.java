package com.frontlinehomes.save2buy.data.land.request;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.type.YesNoConverter;

import java.sql.Timestamp;
@Setter
@Getter
public class AddLandDTO {
    @NonNull
    private String title;
    private String subTitle;
    @NonNull
    private Double priceInSqm;
    private Boolean isArchived;

    private Boolean isSoldOut;
    private Double availableSize;
    private String img1Url;
    private String img2Url;
    @NonNull
    private Double size;
    @NonNull
    private String location;
    @NonNull
    private String neigborhood;
    private String description;

    private String longitude;
    private String latitude;
}
