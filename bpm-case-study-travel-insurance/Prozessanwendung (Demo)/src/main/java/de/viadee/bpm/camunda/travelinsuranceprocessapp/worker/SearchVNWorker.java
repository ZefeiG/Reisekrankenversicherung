package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static de.viadee.bpm.camunda.travelinsuranceprocessapp.service.Authentication.getBasicAuthenticationHeader;

//Klasse die das Suchen nach dem VN im System übernimmt
@Component
public class SearchVNWorker {

    private static final Logger log = LoggerFactory.getLogger(SearchVNWorker.class);

    JsonMapper jsonMapper = new JsonMapper();

    //Worker der nach einem Vertragsnehmer über die ID sucht
    @JobWorker(type = "searchPartnerId", fetchVariables = "travelInsurance")
    public void searchPartnerId(final JobClient client, final ActivatedJob job) throws Exception {

        //Id holen
        String partnerId = new JSONObject(job.getVariables()).getJSONObject("travelInsurance").getJSONObject("policyHolder").getString("id");

        try {
            //Rest get-request
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner/" + partnerId))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .GET()
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            //Rest request senden und Antwort erhalten
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            String statusCode;

            String otherPartner = "";

            //Anhand vom Statuscode entscheiden, was zurückgegeben wird
            if (response.statusCode() == 200) {
                statusCode = "200";
                otherPartner = response.body();
            } else {
                statusCode = "404";
            }

            boolean newPartner = false;

            //Alle zu übergebenden Variablen in eine map packen
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("statusCode", statusCode);
            outputs.put("otherPartner", jsonMapper.writeValueAsString(otherPartner));
            outputs.put("newPartner", newPartner);
            //Worker schließen und Variablen zurücksenden
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPartnerId", e);
        }
    }


    //Worker und VN anhand seiner persönlichen Daten zu suchen
    @JobWorker(type = "searchPersonalData", fetchVariables = {"partnerId", "firstName", "lastName", "birthDate", "iban", "emailAddress", "address", "countryCode"})
    public void searchPersonalData(final JobClient client, final ActivatedJob job) throws Exception {

        try {
            //Variable ins Schema der API bringen
            String newVariables = job.getVariables().replace("postCode", "postalCode");
            String finalVariables = newVariables.replace("country", "countryCode");
            JSONObject newAddress = new JSONObject(finalVariables);

            System.out.println(newAddress);



            String statusCode;


            /*Hier wird mithilfe von postAnfragePartnerSearch eine Rest post-request mit den oben vorbereiteten Daten gesendet
            und anhand von dem zurückbekommenen Statuscode entschieden, ob ein Statuscode entsprechend der Rückgabe gesetzt
            wird oder, wenn die Antwort Statuscode 500 war, die Anfrage erneut versucht wird
            */
            if (postAnfragePartnerSearch(newAddress) == 200) {
                statusCode = "200";
            } else if(postAnfragePartnerSearch(newAddress) == 404) {
                statusCode = "404";
            }else {
                statusCode = String.valueOf(postAnfragePartnerSearch(newAddress));

            }
            boolean newPartner = false;

            //Zu übergebende Variablen in eine Map packen
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("statusCode", statusCode);
            outputs.put("newPartner", newPartner);
            //Worker schließen und Variablen zurücksenden
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at searchPersonalData", e);
        }
    }

    //Worker der einen Partner mit den angegebenen Daten im System erstellt
    @JobWorker(type = "insertNewPartner", fetchVariables = {"partnerId", "firstName", "lastName", "birthDate", "iban", "emailAddress", "address"})
    public void insertNewPartner(final JobClient client, final ActivatedJob job) throws Exception {

        try {
            JSONObject newRequest = new JSONObject(job.getVariables());

            //Rest post-request bauen
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://travel-insurance-api.aws-playground.viadee.cloud/partner"))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(newRequest.toString()))
                    .build();


            HttpClient httpClient = HttpClient.newHttpClient();

            //Request senden und Antwort erhalten
            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            boolean newPartner = true;

            //Zu übergebende Variablen in eine Map packen
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("travelInsuranceId", response.body());
            outputs.put("newPartner", newPartner);
            //Worker schließen und Variablen zurücksenden
            client.newCompleteCommand(job.getKey())
                    .variables(outputs)
                    .send()
                    .join();
        } catch (RestClientException e) {
            log.info("Rest-error at insertNewPartner", e);
        }
    }

    //Worker der die Adressen von den vom VN angegebenen und der im System vorliegenden Daten vergleicht
    @JobWorker(type = "compareAddress", fetchVariables = {"vnAddress", "otherPartnerAddress"})
    public void compareAddress(final JobClient client, final ActivatedJob job) {
        boolean sameAddress;

        //Daten in ihre korrespondierenden Json-Objekte packen
        JSONObject vnAddress = new JSONObject(job.getVariables()).getJSONObject("vnAddress");
        JSONObject otherPartnerAddress = new JSONObject(job.getVariables()).getJSONObject("otherPartnerAddress");

        //checken, ob die Adressen gleich sind
        sameAddress = vnAddress.getString("street").equals(otherPartnerAddress.getString("street")) &&
                vnAddress.getString("number").equals(otherPartnerAddress.getString("number"))&&
                vnAddress.getString("city").equals(otherPartnerAddress.getString("city"))&&
                vnAddress.getString("postCode").equals(otherPartnerAddress.getString("postalCode"))&&
                vnAddress.getString("country").equals(otherPartnerAddress.getString("countryCode"));

        //Worker schließen und Variablen zurücksenden
        client.newCompleteCommand(job.getKey())
                .variable("sameAddress", sameAddress)
                .send()
                .join();
    }

    //Hilfsmethode für eine Rest post-request
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
