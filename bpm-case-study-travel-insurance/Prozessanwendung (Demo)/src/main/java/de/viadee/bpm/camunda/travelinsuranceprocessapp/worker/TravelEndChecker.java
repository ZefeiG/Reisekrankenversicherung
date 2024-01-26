package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TravelEndChecker {
    @JobWorker(type = "check-travel-end", fetchVariables = {"travelStart", "travelEnd"})
    public void checkPlaceOfResidence(final JobClient client, final ActivatedJob job, @Variable String travelStart,
                                      @Variable String travelEnd) {
        boolean travelStartIsBeforeEnd = startIsBeforeEnd(LocalDate.parse(travelStart), LocalDate.parse(travelEnd));
        client.newCompleteCommand(job.getKey())
                .variable("travelStartIsBeforeEnd", travelStartIsBeforeEnd)
                .send()
                .join();
    }

    private boolean startIsBeforeEnd(LocalDate start, LocalDate end) {
        return start.isBefore(end);
    }
}
