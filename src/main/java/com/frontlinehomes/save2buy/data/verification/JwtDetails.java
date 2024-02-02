package com.frontlinehomes.save2buy.data.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDetails {
    private String username;

    private Boolean isAdmin;
}
