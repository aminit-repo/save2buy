package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
public class MonnifyInitResponseBody {
    private String transactionReference;
    private String paymentReference;
    private String merchantName;
    private String apiKey;
    private ArrayList<String> enabledPaymentMethod;
    private String checkoutUrl;
}



