package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.camunda.zeebe.client.api.JsonMapper;
import org.json.JSONObject;
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

        for (Object key : jsonObject.getJSONObject("response").getJSONArray("contentList")) {
            System.out.println(key.toString());
        //diese 'key' ist fuer ganz block wie "23456"
            //das ist ein block wie "23456":{"CountryName":"...","warning":"..."}
            if (jsonObject.getJSONObject("response").getJSONObject(key.toString()).getString("countryName").equalsIgnoreCase(destination)) {
                //wenn inhalt von key "CountryName" gleich als destination in diese block
                hasWarning = jsonObject.getJSONObject("response").getJSONObject(key.toString()).getBoolean("warning");

                client.newCompleteCommand(job)
                .variable("Reisewarnung", hasWarning)
                .send()
                .join();
            }
        } 
        }catch(RestClientException e)
        {e.printStackTrace();}
    }






    @JobWorker(type = "reisewarnung-ablehnung-send")
    public void ReisewarnungAblehnung(final JobClient client, final ActivatedJob job) throws Exception {

        String Warnung="Der Ablehung der Reise";
        String Text="Warnung vorhanden, Reise storniert";

        Partner mail= new Partner();
        EmailService send=new EmailService();
        send.sendSimpleMessage(mail.getMail(),Warnung,Text);
    
    }

    
}

