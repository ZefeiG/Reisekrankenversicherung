package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TravelWarningService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://travelwarning.api.bund.dev/";

    public String getTravelWarnings() {
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            return response;
        } catch (Exception e) {
            return "Error while fetching travel warnings: " + e.getMessage();
        }
    }
}
