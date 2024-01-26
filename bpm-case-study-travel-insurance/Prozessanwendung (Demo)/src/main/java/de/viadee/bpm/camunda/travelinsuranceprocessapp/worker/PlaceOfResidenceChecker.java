package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
@Component
public class PlaceOfResidenceChecker {
    private final Set<String> VALID_COUNTRIES;

    public PlaceOfResidenceChecker() {
        VALID_COUNTRIES = new HashSet<>();
        VALID_COUNTRIES.add("Deutschland");
    }

    @JobWorker(type = "check-place-of-residence", fetchVariables = {"country"})
    public void checkPlaceOfResidence(final JobClient client, final ActivatedJob job, @Variable String countryOfResidence) {
        boolean countryOfResidenceIsValid = VALID_COUNTRIES.contains(countryOfResidence);
        client.newCompleteCommand(job)
                .variable("countryOfResidenceIsValid", countryOfResidenceIsValid)
                .send()
                .join();
    }
}
