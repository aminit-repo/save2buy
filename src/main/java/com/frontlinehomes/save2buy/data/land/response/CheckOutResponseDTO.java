package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.land.request.LandDetailsDTO;
import com.frontlinehomes.save2buy.data.land.request.PaymentPlanDTO;
import com.frontlinehomes.save2buy.data.users.investor.response.InvestorResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CheckOutResponseDTO {

    private InvestorResponseDTO user;

    private LandDetailsDTO land;

    private PaymentPlanResponseDTO paymentPlan;
}
