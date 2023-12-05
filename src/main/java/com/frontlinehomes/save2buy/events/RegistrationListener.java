package com.frontlinehomes.save2buy.events;


import com.frontlinehomes.save2buy.data.email.VerifyEmail;
import com.frontlinehomes.save2buy.data.users.User;
import com.frontlinehomes.save2buy.service.VerificationTokenService;
import com.frontlinehomes.save2buy.service.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);

        String confirmationUrl
                = event.getAppUrl() + "/regitrationConfirm/" + token;


        VerifyEmail verifyEmail= new VerifyEmail(confirmationUrl, user.getEmail(),"","Registration Confirmation");
        try{
            emailService.sendEmail(verifyEmail);
        }catch (Exception e){
            //log the error message
            System.out.println(e.getMessage());
        }

    }
}