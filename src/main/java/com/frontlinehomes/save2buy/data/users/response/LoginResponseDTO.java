package com.frontlinehomes.save2buy.data.users.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {
    private Long id;

    private String token;
}
