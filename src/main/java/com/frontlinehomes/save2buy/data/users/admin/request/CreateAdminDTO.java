package com.frontlinehomes.save2buy.data.users.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frontlinehomes.save2buy.data.users.Gender;
import com.frontlinehomes.save2buy.data.users.Phone;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.data.users.admin.Admin;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import com.frontlinehomes.save2buy.data.users.investor.data.Investor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.type.YesNoConverter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminDTO {

    private String firstName;
    private String lastName;
    private String otherName;

    private String email;

    private String contact;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private List<Scopes> access;

    private String office;

}
