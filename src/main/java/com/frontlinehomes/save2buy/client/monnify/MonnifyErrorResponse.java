package com.frontlinehomes.save2buy.client.monnify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonnifyErrorResponse {
    private Boolean requestSuccessful;
    private String responseMessage;
    private String responseCode;
}
