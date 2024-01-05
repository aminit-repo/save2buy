package com.frontlinehomes.save2buy.data.account.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeCardResponseDTO {
    private Double charge;

    private Boolean isOTPEnabled;

    private String tokenId;

    private String refNumber;

    private Long purchaseId;

}
