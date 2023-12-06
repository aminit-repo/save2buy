package com.frontlinehomes.save2buy.data.users.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpResponseDTO {
    private Long id;
    private String email;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String otherName;
}
