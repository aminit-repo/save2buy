package com.frontlinehomes.save2buy.client.elasticMail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticContent {
   // private ArrayList<ElasticBody> Body= new ArrayList<ElasticBody>();
   @JsonProperty("Merge")
    private ElasticMerge Merge;
    @JsonProperty("EnvelopeFrom")
    private String EnvelopeFrom;
    @JsonProperty("From")
    private String From;
    @JsonProperty("Subject")
    private String Subject;
    @JsonProperty("TemplateName")
    private String TemplateName;


}
