package com.frontlinehomes.save2buy.client.elasticMail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter

@NoArgsConstructor
public class ElasticReceiptMerge {
    private String refNumber;
    private String email;
    private String landTtile;
    private String size;
    private String charge;

    public ElasticReceiptMerge(String refNumber, String email, String landTtile, String size, String charge) {
        this.refNumber = refNumber;
        this.email = email;
        this.landTtile = landTtile;
        this.size = size;
        this.charge = charge;
    }
}
