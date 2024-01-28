package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationMail {
    //Methode zum E-Mail senden
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Fachprojekt2324@web.de");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    // Sendet die Bestätigungs-E-Mail, nachdem der Vertrag gespeichert worden ist
    @JobWorker(type = "ConfirmationMail")
    public void SendMail(final JobClient client, final ActivatedJob job, @Variable String mail) {

        String CC = "Vielen Dank für Ihr Vertrauen in uns!";
        String Text = "Dies ist eine Bestätigungs-E-Mail für Ihre erfolgreiche Buchung der Reiseversicherung.";
        sendSimpleMessage(mail, CC ,Text);
    }
}
