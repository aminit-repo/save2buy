package com.frontlinehomes.save2buy.events.payement;

import com.frontlinehomes.save2buy.data.land.data.InvestorLand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnPaymentCompleteEvent extends ApplicationEvent {

    private String refNumber;

    private InvestorLand investorLand;

    private Double charge;
    public OnPaymentCompleteEvent(InvestorLand investorLand, Double charge, String refNumber ) {
        super(investorLand);
        this.investorLand= investorLand;
        this.refNumber= refNumber;
        this.charge =charge;
    }
}
