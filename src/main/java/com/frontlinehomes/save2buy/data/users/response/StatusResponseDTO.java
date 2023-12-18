package com.frontlinehomes.save2buy.data.users.response;

import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusResponseDTO {
    private Long id;

    private Boolean emailStatus;
    private Boolean profileStatus;

    private Timestamp time;
}
