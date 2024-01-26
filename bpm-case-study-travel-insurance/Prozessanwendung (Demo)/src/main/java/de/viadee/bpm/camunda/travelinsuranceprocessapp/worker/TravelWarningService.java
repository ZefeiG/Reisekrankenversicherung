package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import org.json.JSONObject;


import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelData;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;

public class TravelWarningService {

    public static String checkTravelWarning(TravelData TravelData,Partner Partner) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://travelwarning.api.bund.dev/" + TravelData.getDestination()))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            
            JSONObject jsonResponse = new JSONObject(response.body());
            String warning = jsonResponse.optString("warning", "No warning found");
            
            String mail= Partner.getMail();
            EmailService send=new EmailService();

            send.sendSimpleMessage(mail,warning,warning);
            return warning;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while fetching travel warning";
        }
    }


    
}
