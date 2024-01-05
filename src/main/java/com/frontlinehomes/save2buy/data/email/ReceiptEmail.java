package com.frontlinehomes.save2buy.data.email;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReceiptEmail extends EmailDetails {
    private String charge;

    private String refNumber;

    private String landTitle;

    private String size;

    private String email;

    public ReceiptEmail(String charge, String refNumber, String landTitle, String size, String email, String to, String from, String subject, String envelopeFrom, String template) {
        this.charge = charge;
        this.refNumber = refNumber;
        this.landTitle = landTitle;
        this.size = size;
        this.email = email;

        this.to= to;
        this.from= from;
        this.subject=subject;
        this.envelopeFrom= envelopeFrom;
        this.template= template;
    }
}
