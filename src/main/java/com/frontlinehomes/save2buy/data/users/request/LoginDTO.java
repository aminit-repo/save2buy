package com.frontlinehomes.save2buy.data.users.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
    private String email;
    private String password;
}
