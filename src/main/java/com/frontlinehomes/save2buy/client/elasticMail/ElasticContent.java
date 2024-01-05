package com.frontlinehomes.save2buy.client.elasticMail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticContent<T> {
   @JsonProperty("Merge")
    private T Merge;
    @JsonProperty("EnvelopeFrom")
    private String EnvelopeFrom;
    @JsonProperty("From")
    private String From;
    @JsonProperty("Subject")
    private String Subject;
    @JsonProperty("TemplateName")
    private String TemplateName;
}
