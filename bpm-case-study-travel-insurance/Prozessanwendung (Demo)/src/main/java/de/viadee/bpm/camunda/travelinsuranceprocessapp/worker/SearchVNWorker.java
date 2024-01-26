package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.json.*;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class SearchVNWorker {

    @JobWorker(type = "searchPartnerId")
    public void searchPartnerId(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        String partnerId = travelInsurance.getString("partnerId");

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/" + partnerId))
                    .header("Authorization", getBasicAuthenticationHeader("user3", "SHKw24Ti"))
                    .GET()
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            String statusCode;

            if (response.statusCode() == 200) {
                statusCode = "200";
                JSONObject otherPartner = new JSONObject(response.body());
            } else {
                statusCode = "404";
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @JobWorker(type = "searchPersonalData")
    public void searchPersonalData(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/search"))
                    .header("Authorization", getBasicAuthenticationHeader("user3", "SHKw24Ti"))
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
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    @JobWorker(type = "insertNewPartner")
    public void insertNewPartner(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner"))
                    .header("Authorization", getBasicAuthenticationHeader("user3", "SHKw24Ti"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(travelInsurance.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            travelInsurance.put("partnerId", response.body());
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }
    @JobWorker(type = "compareAddress")
    public void compareAddress(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance, @Variable JSONObject otherPartner) throws Exception{
        String sameAddress;

        if(travelInsurance.getJSONArray("address").similar(otherPartner.getJSONArray("address"))){
            sameAddress = "true";
        }
        else{
            sameAddress = "false";
        }
    }
}
