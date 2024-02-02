package com.frontlinehomes.save2buy.data.account.request;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.account.data.Channel;
import com.frontlinehomes.save2buy.data.account.data.TransactionStatus;
import com.frontlinehomes.save2buy.data.account.data.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransacionRequest {

    private Long purchaseId;

    private BillingType paymentMethod;

    private Double amount;

    private Long paymentPlanId;

    private Channel channel;

    private TransactionStatus transactionStatus;
    private TransactionType transactionType;

    private String transactionId;

}
