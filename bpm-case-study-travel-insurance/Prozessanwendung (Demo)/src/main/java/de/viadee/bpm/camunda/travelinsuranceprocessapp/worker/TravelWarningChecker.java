package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelData;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

public class TravelWarningChecker {

    public HttpResponse<String> response;

    private static final Logger logger =  LoggerFactory.getLogger(TravelWarningChecker.class);
    @JobWorker(type = "Reisewarnung")
    public void Reisewarnung(final JobClient client, final ActivatedJob job, @Variable TravelData destination)throws Exception {

        try {
             HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travelwarning.api.bund.dev/" + destination))
                    .GET()
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();

            response = httpClient.send(getRequest, BodyHandlers.ofString());

            String statusCode;

            JSONObject jsonResponse = new JSONObject();
            if (response.statusCode() == 200) {
                statusCode = "200";
                jsonResponse = new JSONObject(response.body());
            } else {
                statusCode = "404";
            }

            client.newCompleteCommand(job.getKey())
                    .variable("statusCode", statusCode)
                    .variable("otherPartner", jsonResponse)
                    .send()
                    .join();
        } catch (RestClientException e) {
            logger.info("Rest-error at searchPartnerId", e);
        }
    }



    @JobWorker(type = "reisewarnung-ablehnung-send")
    public void ReisewarnungAblehnung(final JobClient client, final ActivatedJob job) throws Exception {

        String Warnung="Der Ablehung der Reise";
            
        String Text = response.body();

        Partner mail= new Partner();
        EmailService send=new EmailService();
        send.sendSimpleMessage(mail.getMail(),Warnung,Text);
    
    }

    
}
