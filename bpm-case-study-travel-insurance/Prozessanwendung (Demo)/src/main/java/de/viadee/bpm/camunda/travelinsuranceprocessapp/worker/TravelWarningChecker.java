package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;
import org.springframework.web.client.RestClientException;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

public class TravelWarningChecker {
    @JobWorker(type = "Reisewarnung")
    public void Reisewarnung(final JobClient client, final ActivatedJob job,@Variable JSONObject travelInsurance) throws Exception{

        try{
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://travelwarning.api.bund.dev/travelwarning"))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
         HttpResponse<String> response = httpClient.send(getRequest, BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());
        //wechseln 'string response.body()'' als Type JSONObject  
        
        boolean hasWarning;
        String destination = travelInsurance.getString("destination");

        for (String key : jsonObject.keySet()) {
        //diese 'key' ist fuer ganz block wie "23456"
            JSONObject countryInfo = jsonObject.getJSONObject(key);
            //das ist ein block wie "23456":{"CountryName":"...","warning":"..."}
            if (countryInfo.getString("CountryName").equalsIgnoreCase(destination)) {
                //wenn inhalt von key "CountryName" gleich als destination in diese block-------------weil destination ist schriftlich
                if(countryInfo.getString("warning")=="true"){
                    //wenn inhalt von key "warning" in gleiche block gleich als "true" (weil "true" here ist String) 
                    hasWarning=true;}
                    else{hasWarning=false;}
                    //wenn ' "warning": true ' boolean hasWarning ist true, umgekehrt.

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

