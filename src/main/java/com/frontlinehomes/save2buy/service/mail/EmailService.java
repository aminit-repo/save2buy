package com.frontlinehomes.save2buy.service.mail;

import com.frontlinehomes.save2buy.data.email.EmailDetails;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private Configuration configuration;
    @Autowired
    private JavaMailSender javaMailSender;


    public void sendEmail(EmailDetails emailDetails) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(emailDetails.getSubject());
        helper.setTo(emailDetails.getTo());
        String emailContent = getEmailContent(emailDetails);
        helper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    String getEmailContent(EmailDetails emailDetails) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("user", emailDetails);
        configuration.getTemplate(emailDetails.getTemplate()).process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }

}
