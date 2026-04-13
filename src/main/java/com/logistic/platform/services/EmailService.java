package com.logistic.platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender eMailSender;

    @Value("${app.mail.from:sch@demomailtrap.com}")
    private String fromAddress;

    @Value("${app.mail.test-recipient:}")
    private String testRecipient;

    public void sendEmail(String to, String subject,String body)
    {
            try{
            SimpleMailMessage message=new SimpleMailMessage();
            String recipient = (testRecipient != null && !testRecipient.isBlank()) ? testRecipient : to;
            System.out.println("EmailService sending mail to: " + recipient + " (requested user email: " + to + ")");
            message.setTo(recipient);
            message.setText(body);
            message.setSubject(subject);
            message.setFrom(fromAddress);
            eMailSender.send(message);
            }catch(MailException e)
            {
                throw e;
            }
    }

}
