package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
public class TravelStartChecker {
    
    static boolean travelStartIsInFuture;
    
    @JobWorker(type = "check-travel-start", fetchVariables = {"travelStart"})
    public void checkPlaceOfResidence(final JobClient client, final ActivatedJob job, @Variable String travelStart) {
        travelStartIsInFuture = isInFuture(LocalDate.parse(travelStart));
        client.newCompleteCommand(job.getKey())
                .variable("travelStartIsValid", travelStartIsInFuture)
                .send()
                .join();
    }

    private boolean isInFuture(LocalDate date) {
        return LocalDate.now().isBefore(date);
    }
}
