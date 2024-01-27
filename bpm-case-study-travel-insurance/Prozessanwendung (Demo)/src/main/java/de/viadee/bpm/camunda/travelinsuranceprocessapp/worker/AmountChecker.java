package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

@Component
public class AmountChecker {

static boolean amountLessThan7;

    @JobWorker(type = "check-amount")
    public void checkAmount(final JobClient client, final ActivatedJob job, @Variable int numberInsuredPartners){

        amountLessThan7 = numberInsuredPartners < 7;

        client.newCompleteCommand(job.getKey())
                .variable("amountOfInsuredPartners", amountLessThan7)
                .send()
                .join();
    }
}
