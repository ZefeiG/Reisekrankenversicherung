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

public class AmountChecker {

static boolean AmountLessThan7;

    @JobWorker(type = "check-amount")
    public void checkAmount(final JobClient client, final ActivatedJob job, @Variable JSONObject travelInsurance) throws Exception {

        String policyHolder = travelInsurance.getString("policyHolder");

        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/search"+ policyHolder))
                    .header("Authorization", getBasicAuthenticationHeader("user3", "SHKw24Ti"))
                    //.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(travelInsurance.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            
            String[] policyHolderArray = response.body().split(",");
            
            boolean AmountLessThan7 = policyHolderArray.length <= 7;

         }catch (RestClientException e) {
            e.printStackTrace();
         }

   
        
    }
}
