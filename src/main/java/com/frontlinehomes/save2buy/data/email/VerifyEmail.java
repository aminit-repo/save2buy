package com.frontlinehomes.save2buy.data.email;

import lombok.Getter;

@Getter
public class VerifyEmail extends EmailDetails {
    private String url;

    public VerifyEmail(String url, String to, String from, String subject) {
        this.url = url;
        this.to= to;
        this.from= from;
        this.subject=subject;
        this.template="verify.ftlh";
    }

}
