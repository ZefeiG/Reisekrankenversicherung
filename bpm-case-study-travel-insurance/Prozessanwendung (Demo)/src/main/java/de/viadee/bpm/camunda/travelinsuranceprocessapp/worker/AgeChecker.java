package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class AgeChecker {
    private final int AGE_OF_ADULTHOOD_IN_GERMANY = 18;

    @JobWorker(type = "check-age", fetchVariables = {"birthday"})
    public void checkAge(final JobClient client, final ActivatedJob job, @Variable String birthday) {
        int age = convertBirthdayToAge(LocalDate.parse(birthday));
        boolean isAdult = AGE_OF_ADULTHOOD_IN_GERMANY <= age;
        client.newCompleteCommand(job)
                .variable("policyHolderIsAdult", isAdult)
                .send()
                .join();
    }

    private int convertBirthdayToAge(LocalDate birthday) {
        LocalDate now = LocalDate.now();
        return Period.between(birthday, now).getYears();
    }
}
