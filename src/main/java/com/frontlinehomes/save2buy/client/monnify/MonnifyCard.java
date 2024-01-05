package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonnifyCard {
    private String number;
    private String expiryMonth;
    private String expiryYear;
    private String pin;

    private String cvv;
}
