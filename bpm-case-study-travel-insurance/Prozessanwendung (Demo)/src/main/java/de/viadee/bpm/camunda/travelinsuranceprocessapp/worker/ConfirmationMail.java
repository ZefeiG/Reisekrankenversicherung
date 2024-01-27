package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationMail {

    // Sendet die Bestätigungs-E-Mail, nachdem der Vertrag gespeichert worden ist
    @JobWorker(type = "ConfirmationMail")
    public void SendMail() {
        Partner mail = new Partner() ;
        EmailService send = new EmailService();

        String CC = "Vielen Dank für Ihr Vertrauen in uns!";
        String Text = "Dies ist eine Bestätigungs-E-Mail für Ihre erfolgreiche Buchung der Reiseversicherung.";
        send.sendSimpleMessage(mail.getMail(), CC ,Text);
    }
}
