package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;


import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class DurationBetweenDates {

    // Berechnet die Dauer der Reise in Tage

    @JobWorker(type = "duration")
    public void getDuration(final JobClient client, final ActivatedJob job, @Variable LocalDate start, @Variable LocalDate end){

        long duration = ChronoUnit.DAYS.between(start, end);
        System.out.println(duration);
        client.newCompleteCommand(job)
                .variable("duration",duration)
                .send()
                .join();
    }
}
