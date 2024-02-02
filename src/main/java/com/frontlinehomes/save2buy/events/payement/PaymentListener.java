package com.frontlinehomes.save2buy.events.payement;

import com.frontlinehomes.save2buy.client.elasticMail.ElasticMailClient;
import com.frontlinehomes.save2buy.data.email.ReceiptEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener implements ApplicationListener<OnPaymentCompleteEvent> {
    @Autowired
    private ElasticMailClient elasticMailClient;
    private static Logger log = LogManager.getLogger( PaymentListener.class);
    @Override
    public void onApplicationEvent(OnPaymentCompleteEvent event) {
        this.paymentCompletion(event);
    }

    private void paymentCompletion(OnPaymentCompleteEvent event){

        //create a new object of receiptEmail
        ReceiptEmail receiptEmail= new ReceiptEmail(event.getCharge().toString(), event.getRefNumber(),
                event.getInvestorLand().getLand().getTitle(),
                event.getInvestorLand().getSize().toString(), event.getInvestorLand().getInvestor().getUser().getEmail(),
                event.getInvestorLand().getInvestor().getUser().getEmail(), "no-reply@save2buy.ng", "Save2buy - Payment Notification", "no-reply <no-reply@save2buy.ng>",
                "payment-confirmation");
        try{
            elasticMailClient.sendTransactionEmail(receiptEmail);
            log.info("PaymentListener:paymentCompletion email sent successfully to "+event.getInvestorLand().getInvestor().getUser().getEmail());
        }catch (Exception e){
            log.error("RegistrationListener:confirmRegistration "+e.getMessage());
        }
    }

}
