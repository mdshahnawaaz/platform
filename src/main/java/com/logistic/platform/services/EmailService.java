package com.logistic.platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender eMailSender;

    public void sendEmail(String to, String subject,String body)
    {
            try{   
            SimpleMailMessage message=new SimpleMailMessage();
            // message.setFrom("ilh@demomailtrap.com");
            message.setTo("parveenshahin038@gmail.com");
            message.setText("hi ,aksie ho kaise chal rahah");
            message.setSubject("hello");
            message.setFrom("sch@demomailtrap.com");
            eMailSender.send(message);
            }catch(Exception e)
            {
                e.getStackTrace();
                System.out.println(e);
            }
        
    }

}
