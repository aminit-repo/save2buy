package com.frontlinehomes.save2buy.data.users.investor.response;

import com.frontlinehomes.save2buy.data.users.Gender;
import com.frontlinehomes.save2buy.data.users.Phone;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class InvestorResponseDTO {
    private Long id;

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

    private String firstName;
    private String lastName;
    private String otherName;
    private String email;

    private String primaryLine;

    private Gender gender;

    private String purposeOfAccount;
}
