package com.frontlinehomes.save2buy.client.elasticMail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticMail {

  private String TransactionID;
   private  String MessageID;

   private String Error;
}
