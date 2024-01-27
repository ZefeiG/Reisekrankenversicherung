package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.json.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static de.viadee.bpm.camunda.travelinsuranceprocessapp.service.Authentication.getBasicAuthenticationHeader;
@Component
public class AmountChecker {

static boolean amountLessThan7;

    @JobWorker(type = "check-amount")
    public void checkAmount(final JobClient client, final ActivatedJob job, @Variable int numberInsuredPartners) throws Exception {

        amountLessThan7 = numberInsuredPartners < 7;

        client.newCompleteCommand(job.getKey())
                .variable("amountOfInsuredPartners", amountLessThan7)
                .send()
                .join();
    }
}
