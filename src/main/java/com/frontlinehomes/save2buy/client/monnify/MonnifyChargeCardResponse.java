package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonnifyChargeCardResponse {
    private Boolean requestSuccessful;
    private String responseMessage;
    private String responseCode;
    private MonnifyChargeCardResponseBody responseBody;

}


