package com.frontlinehomes.save2buy.data.land.request;

import com.frontlinehomes.save2buy.data.land.data.DurationType;
import com.frontlinehomes.save2buy.data.land.data.Frequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
public class PaymentPlanDTO {

    //this is optional, lands can be given names
    private String name;
    //total size to be purchased
    private Double SizeInSqm;

    //Amount is the total amount of land for the specified size
    private Double amount;

    //duration of payment (week, month, day)
    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    private  Integer durationLength;

    private Integer durationWeight;

    //for DEPOSIT Frequency type, it is the initial deposit. but for frequency (WEEKLY, DAILY, MONTHLY) type it for current charge
    private Double charges;
    //frequency
    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    //the total size

    private String note;


}
