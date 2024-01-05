package com.frontlinehomes.save2buy.data.account.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChargeCardRequestDTO {

    private Long  purchaseId;
    private String refNumber;

    private String number;
    private String expiryMonth;
    private String expiryYear;
    private String pin;

    private String cvv;
}
