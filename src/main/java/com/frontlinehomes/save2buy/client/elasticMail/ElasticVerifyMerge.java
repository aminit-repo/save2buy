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
public class ElasticVerifyMerge {
   @JsonProperty("Verify")
   public String Verify;

}
