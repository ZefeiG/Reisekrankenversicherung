package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.time.LocalDate;
import java.time.Period;

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
public class AntragsdatenChecker {

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
    private final int AGE_OF_ADULTHOOD_IN_GERMANY = 18; //für age check


    //Task fuer ReiseKosten check
    @JobWorker(type = "check-travel-cost", fetchVariables = {"travelCost"})
    public void checkTravelCost(final JobClient client, final ActivatedJob job, @Variable int travelCost) {
        //checke, dass Reisekosten nicht 0 sind
        boolean travelCostIsPositive = travelCost > 0;
        client.newCompleteCommand(job)
                .variable("travelCostIsValid", travelCostIsPositive)
                .send()
                .join();
                
    }


//Task fuer Reiseende check
     @JobWorker(type = "check-travel-end", fetchVariables = {"travelStart", "travelEnd"})
    public void checkTravelEnd(final JobClient client, final ActivatedJob job, @Variable String travelStart,
                                      @Variable String travelEnd) {
        //checken, wie lang die Reisedauer ist
        boolean travelStartIsBeforeEnd = startIsBeforeEnd(LocalDate.parse(travelStart), LocalDate.parse(travelEnd));
        client.newCompleteCommand(job)
                .variable("travelStartIsBeforeEnd", travelStartIsBeforeEnd)
                .send()
                .join();
    }

    private boolean startIsBeforeEnd(LocalDate start, LocalDate end) {
        return start.isBefore(end);
    }



//Task fuer Reisestart check
    @JobWorker(type = "check-travel-start", fetchVariables = {"travelStart"})
    public void checkTravelStart(final JobClient client, final ActivatedJob job, @Variable String travelStart) {
        //checken, ob der Reisestart in der Zukunft liegt
        boolean travelStartIsInFuture = isInFuture(LocalDate.parse(travelStart));
        client.newCompleteCommand(job)
                .variable("travelStartIsValid", travelStartIsInFuture)
                .send()
                .join();
    }

    private boolean isInFuture(LocalDate date) {
        return LocalDate.now().isBefore(date);
    }



// Wenn ReiseDaten nicht uebereinstimmen, senden Ablehung
    @JobWorker(type = "ablehnungSenden")
    public void AblehnungSend(final JobClient client, final ActivatedJob job, @Variable String mail){

        String Ablehnung="Der Ablehnung der Reise";
        String Text="Reisedaten nicht mit der Politik übereinstimmen";
        sendSimpleMessage(mail,Ablehnung,Text);

    }


// Task fuer age check
    @JobWorker(type = "check-age", fetchVariables = {"birthday"})
    public void checkAge(final JobClient client, final ActivatedJob job, @Variable String birthday) {
        //checken, ob VN mindestens 18 Jahre alt ist
        int age = convertBirthdayToAge(LocalDate.parse(birthday));
        boolean isAdult = AGE_OF_ADULTHOOD_IN_GERMANY <= age;
        client.newCompleteCommand(job)
                .variable("policyHolderIsAdult", isAdult)
                .send()
                .join();
    }

    private int convertBirthdayToAge(LocalDate birthday) {
        LocalDate now = LocalDate.now();
        return Period.between(birthday, now).getYears();
    }



// Task fuer Place check

    @JobWorker(type = "check-place-of-residence", fetchVariables = {"country"})
    public void checkPlaceOfResidence(final JobClient client, final ActivatedJob job, @Variable String country) {
        //checken, ob Heimatort deutschland ist
        boolean countryOfResidenceIsValid = country.equals("Deutschland");
        client.newCompleteCommand(job)
                .variable("countryOfResidenceIsValid", countryOfResidenceIsValid)
                .send()
                .join();
    }
    

// Task fuer VN amount check
    @JobWorker(type = "check-amount")
    public void checkAmount(final JobClient client, final ActivatedJob job, @Variable int numberInsuredPartners){
        //checken, ob es weniger als 7 Vertragspartner sind
        boolean amountLessThan7 = numberInsuredPartners < 7;

        client.newCompleteCommand(job.getKey())
                .variable("amountOfInsuredPartners", amountLessThan7)
                .send()
                .join();
    }


// Wenn Persoenliche Daten nicht uebereinstimmen, senden Ablehung 
    @JobWorker(type = "ablehnungWiederSenden")
    public void AblehnungWiederSend(final JobClient client, final ActivatedJob job, @Variable String mail){


        String Ablehnung="Der Ablehnung der Reise";
        String Text="Persönliche Daten nicht mit der Politik übereinstimmen";
        sendSimpleMessage(mail,Ablehnung,Text);

    }

}
