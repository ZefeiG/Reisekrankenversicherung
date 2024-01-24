package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.TravelData;

@RestController
public class TravelWarningService {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/checkTravelWarning")
    public String checkTravelWarning(@RequestBody TravelData travelDate) {
        String url = "https://travelwarning.api.bund.dev/api/" + travelDate.getDestination();
        String response = restTemplate.getForObject(url, String.class);
        if (response != null && response.length() > 0) {
            try {
                return response;
            } catch (Exception e) {
                return "Error while fetching travel warnings: " + e.getMessage();
            }
        }
        return "";
    }
        
    }


