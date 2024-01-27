package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.camunda.zeebe.spring.client.annotation.Variable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelData;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
@Component
public class TravelWarningChecker {

    
    @JobWorker(type = "Reisewarnung")
    public void Reisewarnung(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception{

        try{
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://travelwarning.api.bund.dev/travelwarning"))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(getRequest, BodyHandlers.ofString());


        TravelData ziel= new TravelData();
        boolean hasWarning;

        JSONArray warnings = new JSONArray(response.body());
        for (int i = 0; i < warnings.length(); i++) {
            JSONObject warning = warnings.getJSONObject(i);
            String countryname = warning.getString("CountryName");

            if (countryname.equalsIgnoreCase(ziel.getDestination())) {
                hasWarning=true;
                break;
            }
        }
            hasWarning=false;

            client.newCompleteCommand(job)
                .variable("Reisewarnung", hasWarning)
                .send()
                .join();

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
