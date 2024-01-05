package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
public class MonnifyInitRequest {
    private  Double amount;
    private String customerName;
    private String customerEmail;
    private String paymentReference;
    private String paymentDescription;
    private String currencyCode;
    private  String contractCode;
    private String redirectUrl;
    private ArrayList paymentMethods= new ArrayList<>();
}


