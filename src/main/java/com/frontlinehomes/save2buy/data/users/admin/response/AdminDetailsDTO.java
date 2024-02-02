package com.frontlinehomes.save2buy.data.users.admin.response;

import com.frontlinehomes.save2buy.data.users.Gender;
import com.frontlinehomes.save2buy.data.users.admin.Scopes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;

    private String email;

    private List<String> contacts;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String office;
}
