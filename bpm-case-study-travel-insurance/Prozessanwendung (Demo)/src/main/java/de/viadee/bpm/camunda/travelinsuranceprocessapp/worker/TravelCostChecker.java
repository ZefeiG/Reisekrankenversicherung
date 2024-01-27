package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

@Component
public class TravelCostChecker {

static boolean travelCostIsPositive;
    
    @JobWorker(type = "check-travel-cost", fetchVariables = {"travelCost"})
    public void checkTravelCost(final JobClient client, final ActivatedJob job, @Variable int travelCost) {
        travelCostIsPositive = travelCost > 0;
        client.newCompleteCommand(job.getKey())
                .variable("travelCostIsValid", travelCostIsPositive)
                .send()
                .join();

    }
}
