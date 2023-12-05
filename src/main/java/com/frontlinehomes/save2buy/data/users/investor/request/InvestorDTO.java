package com.frontlinehomes.save2buy.data.users.investor.request;

import com.frontlinehomes.save2buy.data.users.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvestorDTO {

    private String address;

    private String nationality;

    private String occupation;

    private String employeeName;

    private String employeeAddress;

    private String sourceOfIncome;

    private String monthlyIncomeEstimate;

    private String nextOfKinName;

    private  String nextOfKinPhone;

    private String nextOfKinAddress;

    private String nextOfKinRelationship;

    private User user;

}
