package de.viadee.bpm.camunda.travelinsuranceprocessapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("yourEmail@example.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        mailSender.send(message);
    }
}
