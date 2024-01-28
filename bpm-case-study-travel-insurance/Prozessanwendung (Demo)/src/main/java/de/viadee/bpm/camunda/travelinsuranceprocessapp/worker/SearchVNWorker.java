package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelInsuranceProcessException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static de.viadee.bpm.camunda.travelinsuranceprocessapp.service.Authentication.getBasicAuthenticationHeader;

//Klasse die
@Component
public class SearchVNWorker {

    private static final Logger log = LoggerFactory.getLogger(SearchVNWorker.class);

    JsonMapper jsonMapper = new JsonMapper();

    @JobWorker(type = "searchPartnerId", fetchVariables = "travelInsurance")
    public void searchPartnerId(final JobClient client, final ActivatedJob job) throws Exception {

        String partnerId = new JSONObject(job.getVariables()).getJSONObject("travelInsurance").getJSONObject("policyHolder").getString("id");

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/" + partnerId))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .GET()
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            String statusCode;

            String otherPartner = "";
            if (response.statusCode() == 200) {
                statusCode = "200";
                otherPartner = response.body();
            } else {
                statusCode = "404";
            }

            boolean newPartner = false;
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("statusCode", statusCode);
            outputs.put("otherPartner", jsonMapper.writeValueAsString(otherPartner));
            outputs.put("newPartner", newPartner);
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPartnerId", e);
        }
    }


    @JobWorker(type = "searchPersonalData", fetchVariables = {"partnerId", "firstName", "lastName", "birthDate", "iban", "emailAddress", "address", "countryCode"})
    public void searchPersonalData(final JobClient client, final ActivatedJob job) throws Exception {

        try {

            String newVariables = job.getVariables().replace("postCode", "postalCode");
            String finalVariables = newVariables.replace("country", "countryCode");
            JSONObject newAddress = new JSONObject(finalVariables);

            System.out.println(newAddress);



            String statusCode;


            if (postAnfragePartnerSearch(newAddress) == 200) {
                statusCode = "200";
            } else if(postAnfragePartnerSearch(newAddress) == 404) {
                statusCode = "404";
            }else {
                statusCode = String.valueOf(postAnfragePartnerSearch(newAddress));

            }
            boolean newPartner = false;
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("statusCode", statusCode);
            outputs.put("newPartner", newPartner);
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPersonalData", e);
        }
    }

    @JobWorker(type = "insertNewPartner", fetchVariables = {"partnerId", "firstName", "lastName", "birthDate", "iban", "emailAddress", "address"})
    public void insertNewPartner(final JobClient client, final ActivatedJob job) throws Exception {

        try {
            JSONObject newRequest = new JSONObject(job.getVariables());

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner"))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(newRequest.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            boolean newPartner = true;

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("travelInsuranceId", response.body());
            outputs.put("newPartner", newPartner);
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at insertNewPartner", e);
        }
    }

    @JobWorker(type = "compareAddress", fetchVariables = {"vnAddress", "otherPartnerAddress"})
    public void compareAddress(final JobClient client, final ActivatedJob job) {
        boolean sameAddress;

        String newVariables = job.getVariables().replace("postCode", "postalCode");
        String finalVariables = newVariables.replace("country", "countryCode");


        sameAddress = new JSONObject(finalVariables).similar(new JSONObject(finalVariables));
        client.newCompleteCommand(job.getKey())
                .variable("sameAddress", sameAddress)
                .send()
                .join();
    }

    private int postAnfragePartnerSearch(JSONObject body) throws Exception{

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/search"))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();
    }

}
