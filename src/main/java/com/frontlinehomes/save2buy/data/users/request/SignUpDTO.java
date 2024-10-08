package com.frontlinehomes.save2buy.data.users.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpDTO {

    private Long id;
    private String email;
    private String password;
    private String confirmPassword;
    private String myReferee;
    private String firstName;
    private String lastName;
    private String primaryLine;

    private String code;
}
