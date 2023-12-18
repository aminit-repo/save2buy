package com.frontlinehomes.save2buy.data.users.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String email;

    private String token;
}
