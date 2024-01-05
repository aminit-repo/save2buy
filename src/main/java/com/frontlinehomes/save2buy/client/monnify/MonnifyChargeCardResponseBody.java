package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonnifyChargeCardResponseBody {
    private String status;
    private String message;

    private String transactionReference;

    private String paymentReference;
    private Double authorizedAmount;
    private MonnifyChargeCardResponseOTPData otpData = new MonnifyChargeCardResponseOTPData();
    private MonnifyChargeCardResponseSecure3dData secure3dData = new MonnifyChargeCardResponseSecure3dData();

}


