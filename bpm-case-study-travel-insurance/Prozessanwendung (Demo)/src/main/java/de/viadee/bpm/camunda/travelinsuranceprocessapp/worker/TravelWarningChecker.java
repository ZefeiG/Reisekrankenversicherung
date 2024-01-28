package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.camunda.zeebe.client.api.JsonMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
@Component
public class TravelWarningChecker {

    @Autowired
    private JavaMailSender emailSender;

    //Methode zum E-Mail versenden
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Fachprojekt2324@web.de");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    @JobWorker(type = "Reisewarnung")
    public void Reisewarnung(final JobClient client, final ActivatedJob job,@Variable String destination) throws Exception{

        try{
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://www.auswaertiges-amt.de/opendata/travelwarning"))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(getRequest, BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());
        //wechseln 'string response.body()'' als Type JSONObject  
        
        boolean hasWarning;

        //Alle Länder in der Liste durchlaufen
        for (Object key : jsonObject.getJSONObject("response").getJSONArray("contentList")) {
            //Prüfen, ob das Land das Reiseziel ist
            if (jsonObject.getJSONObject("response").getJSONObject(key.toString()).getString("countryName").equalsIgnoreCase(destination)) {
                //Prüfen, ob beim Reiseziel eine Reisewarnung vorliegt
                hasWarning = jsonObject.getJSONObject("response").getJSONObject(key.toString()).getBoolean("warning");

                //Worker schließen und Variablen zurücksenden
                client.newCompleteCommand(job)
                .variable("Reisewarnung", hasWarning)
                .send()
                .join();
            }
        } 
        }catch(RestClientException e)
        {e.printStackTrace();}
    }






    //Ablehnung schicken, wenn eine Reisewarnung vorliegt
    @JobWorker(type = "reisewarnung-ablehnung-send")
    public void ReisewarnungAblehnung(final JobClient client, final ActivatedJob job, @Variable String mail) {

        String Warnung="Ablehnung der Reiseversicherung";
        String Text="Da eine Reisewarnung für Ihr Reiseziel vorliegt, können wir Sie leider nicht versichern";

        sendSimpleMessage(mail,Warnung,Text);

    }

    
}

