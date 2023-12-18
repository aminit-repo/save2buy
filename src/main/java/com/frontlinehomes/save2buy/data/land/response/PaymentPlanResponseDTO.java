package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.data.land.data.Frequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Setter
@Getter
public class PaymentPlanResponseDTO {
    private Long id;
    //this is optional, names can be given to a plan
    private String name;
    //total size to be purchased
    private Double SizeInSqm;

    //Setting SizeInSqm meters makes this field compulsory, as it is the cost of land specified in sizeInSqm
    private Double amount;

    //duration of payment
    private Integer durationLength;
    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    private Integer durationWeight;

    //for DEPOSIT Frequency type, setting this fields enables  initial deposit option to be shown. (300K initial Deposit)
    private Double charges;
    //frequency
    private Boolean showNote;

    private Boolean showDurationAndFrequency;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    //the total size

    private String note;

    private Timestamp creationDate;
}
