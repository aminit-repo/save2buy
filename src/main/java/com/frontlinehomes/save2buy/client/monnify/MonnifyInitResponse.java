package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

import java.sql.Struct;

@Setter
@Getter
public class MonnifyInitResponse {
    private Boolean requestSuccessful;
    private String responseMessage;
    private String responseCode;
    private MonnifyInitResponseBody responseBody;
}

