package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonnifyChargeCardRequest {
    private String transactionReference;
    private String collectionChannel;
    private MonnifyCard card= new MonnifyCard();

    private String tokenId;

    private String token;

}


