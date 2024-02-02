package com.frontlinehomes.save2buy.data.account.response;

import com.frontlinehomes.save2buy.data.account.data.BillingType;
import com.frontlinehomes.save2buy.data.account.data.Channel;
import com.frontlinehomes.save2buy.data.account.data.TransactionStatus;
import com.frontlinehomes.save2buy.data.account.data.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class CreateTransactionResponseDTO {
    private Long id;

    private Long purchaseId;

    private BillingType paymentMethod;

    private Double amount;

    private Long paymentPlanId;

    private Channel channel;

    private TransactionStatus transactionStatus;
    private TransactionType transactionType;

    private String transactionId;

    private String refNumber;

    private Timestamp createdTime;
}
