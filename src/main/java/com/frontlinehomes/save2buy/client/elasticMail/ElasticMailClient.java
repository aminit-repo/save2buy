package com.frontlinehomes.save2buy.client.elasticMail;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.frontlinehomes.save2buy.data.email.EmailDetails;
import com.frontlinehomes.save2buy.data.email.VerifyEmail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Component
public class ElasticMailClient {


    @Value("${emailService.apiKey}")
    private String apiKey;

    private WebClient webClient;

    private static Logger log = LogManager.getLogger(ElasticMailClient.class);


    public ElasticMailClient(WebClient.Builder builder) {

        this.webClient = builder.baseUrl("https://api.elasticemail.com/v4/emails/transactional").build();
    }

    public Boolean sendTransactionEmail(EmailDetails emailDetails) throws RuntimeException{
        //create ElasticRecipient
        ElasticRecipient elasticRecipient= new ElasticRecipient();

        //create ElasticContent
        ElasticContent elasticContent= new ElasticContent();
        ObjectMapper mapper= new ObjectMapper();



        if(emailDetails instanceof VerifyEmail){
            //add to
            log.info("instance of verifyEmail");
            ArrayList to= new ArrayList<String>();
            to.add(emailDetails.getTo());
            elasticRecipient.setTo(to);


            //create merge
            ElasticMerge elasticMerge= new ElasticMerge(((VerifyEmail) emailDetails).getName(), ((VerifyEmail) emailDetails).getUrl());
            


            elasticContent.setEnvelopeFrom(emailDetails.getEnvelopeFrom());

            elasticContent.setFrom(emailDetails.getFrom());
            elasticContent.setSubject(emailDetails.getSubject());
            elasticContent.setTemplateName(emailDetails.getTemplate());
            elasticContent.setMerge(elasticMerge);

        }


        //create ElasticTransactionEmail
        ElasticTransactionEmail elasticTransactionEmail= new ElasticTransactionEmail();
        elasticTransactionEmail.setContent(elasticContent);
        elasticTransactionEmail.setRecipients(elasticRecipient);
        log.info(apiKey);




        try{
            ElasticMail elasticMail= webClient.post()
                    .header("X-ElasticEmail-ApiKey", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(elasticTransactionEmail)
                    .retrieve()
                    .bodyToMono(ElasticMail.class)
                    .block();
            log.info("ElasticMailClient: sendTransactionEmail: request sent with message ID"+elasticMail.getMessageID());
            return true;

        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }

    }






}
