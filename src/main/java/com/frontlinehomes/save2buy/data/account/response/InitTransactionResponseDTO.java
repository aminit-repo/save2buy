package com.frontlinehomes.save2buy.data.account.response;

import com.frontlinehomes.save2buy.data.ResponseDTO;
import com.frontlinehomes.save2buy.data.account.data.BillingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InitTransactionResponseDTO {
    private Long paymentId;
    private String refNumber;
    private BillingType paymentMethod;
}
