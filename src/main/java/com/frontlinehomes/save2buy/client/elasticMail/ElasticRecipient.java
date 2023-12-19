package com.frontlinehomes.save2buy.client.elasticMail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticRecipient {
    @JsonProperty("To")
    private ArrayList To= new ArrayList<String>();


}
