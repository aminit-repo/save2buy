package com.frontlinehomes.save2buy.events.registration;


import com.frontlinehomes.save2buy.client.elasticMail.ElasticMailClient;

import com.frontlinehomes.save2buy.data.email.VerifyEmail;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.service.VerificationTokenService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    //smtp email service
   /* @Autowired
    private EmailService emailService;
    */

    //elastic mail sender
    @Autowired
    private ElasticMailClient elasticMailClient;

    @Autowired
    private VerificationTokenService verificationTokenService;

    private static Logger log = LogManager.getLogger( RegistrationListener.class);


    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);

        String confirmationUrl
                = event.getAppUrl()  + token;

        //user.getEmail()
        VerifyEmail verifyEmail= new VerifyEmail(confirmationUrl,user.getEmail() ,"no-reply@save2buy.ng","Save2buy Email Verification", "no-reply <no-reply@save2buy.ng>","email-verification");
        verifyEmail.setName(user.getFirstName()!=null? user.getFirstName(): " ");
        try{
            elasticMailClient.sendTransactionEmail(verifyEmail);
            log.info("RegistrationListener:confirmRegistration email sent successfully to "+user.getEmail());
        }catch (Exception e){
            //log the error message
            log.error("RegistrationListener:confirmRegistration "+e.getMessage());
        }

    }
}