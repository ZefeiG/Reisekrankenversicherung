package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static de.viadee.bpm.camunda.travelinsuranceprocessapp.service.Authentication.getBasicAuthenticationHeader;

@Component
public class SearchVNWorker {

    private static final Logger log = LoggerFactory.getLogger(SearchVNWorker.class);
    @JobWorker(type = "searchPartnerId")
    public void searchPartnerId(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        String partnerId = travelInsurance.getString("partnerId");

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/" + partnerId))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .GET()
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            String statusCode;

            JSONObject otherPartner = new JSONObject();
            if (response.statusCode() == 200) {
                statusCode = "200";
                otherPartner = new JSONObject(response.body());
            } else {
                statusCode = "404";
            }
            client.newCompleteCommand(job.getKey())
                    .variable("statusCode", statusCode)
                    .variable("otherPartner", otherPartner)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPartnerId", e);
        }
    }


    @JobWorker(type = "searchPersonalData")
    public void searchPersonalData(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/search"))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(travelInsurance.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            String statusCode;

            if (response.statusCode() == 200) {
                statusCode = "200";
                travelInsurance = new JSONObject(response.body());
            } else {
                statusCode = "404";
            }

            client.newCompleteCommand(job.getKey())
                    .variable("statusCode", statusCode)
                    .variable("travelInsurance", travelInsurance)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPersonalData", e);
        }
    }

    @JobWorker(type = "insertNewPartner")
    public void insertNewPartner(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner"))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(travelInsurance.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            travelInsurance.put("partnerId", response.body());
            client.newCompleteCommand(job.getKey())
                    .variable("travelInsurance", travelInsurance)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at insertNewPartner", e);
        }
    }
    @JobWorker(type = "compareAddress")
    public void compareAddress(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance, @Variable JSONObject otherPartner){
        boolean sameAddress;

        sameAddress = travelInsurance.getJSONArray("address").similar(otherPartner.getJSONArray("address"));
        client.newCompleteCommand(job.getKey())
                .variable("sameAddress", sameAddress)
                .send()
                .join();
    }
}
