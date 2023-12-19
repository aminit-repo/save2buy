package com.frontlinehomes.save2buy.data.email;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyEmail extends EmailDetails {
    private String url;
    private String name;

    public VerifyEmail(String url, String to, String from, String subject, String envelopeFrom, String template) {
        this.url = url;
        this.to= to;
        this.from= from;
        this.subject=subject;
        this.template=template;
        this.envelopeFrom= envelopeFrom;
    }

}
