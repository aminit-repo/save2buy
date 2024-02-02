package com.frontlinehomes.save2buy.data.account.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private Long userId;

    private String email;
}
