package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

public class TravelCostChecker {
    @JobWorker(type = "check-travel-cost", fetchVariables = {"travelCost"})
    public void checkTravelCost(final JobClient client, final ActivatedJob job, @Variable int cost) {
        boolean travelCostIsPositive = cost > 0;
        client.newCompleteCommand(job)
                .variable("travelCostIsValid", travelCostIsPositive)
                .send()
                .join();
    }
}
