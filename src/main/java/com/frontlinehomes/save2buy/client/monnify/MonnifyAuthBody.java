package com.frontlinehomes.save2buy.client.monnify;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonnifyAuthBody {
    private String accessToken;

    private Long expiresIn;
}
