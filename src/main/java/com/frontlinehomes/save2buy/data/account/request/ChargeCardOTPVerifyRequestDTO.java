package com.frontlinehomes.save2buy.data.account.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChargeCardOTPVerifyRequestDTO {
    private String refNumber;
    private String tokenId;
    private String token;

    private Long purchaseId;

}

