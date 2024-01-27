package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;


import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DetailsDerReise {

    // checked was genau die destination ist

    @JobWorker(type="DetailsReise")
    public void DetailsReiseBestimmen(final JobClient client, final ActivatedJob job, @Variable String destination){
        String Reiseziel = "";
        String Germany = "Deutschland";
        String[] EULaender = {"Belgien", "Bulgarien", "Dänemark", "Estland", "Finnland", "Frankreich", "Griechenland",
                            "Großbritannien", "Irland", "Italien", "Kroatien", "Lettland", "Litauen", "Luxemburg",
                            "Malta", "Niederlande", "Österreich", "Polen", "Portugal", "Rumänien", "Schweden",
                            "Slowakei", "Slowenien", "Spanien", "Tschechische Republik", "Ungarn", "Zypern"};
        List<String> list = Arrays.asList(EULaender);

        if(Germany.equalsIgnoreCase(destination)){
            client.newCompleteCommand(job)
                    .variable("Deutschland", Reiseziel)
                    .send()
                    .join();
        }
        else if(list.contains(destination)){
            client.newCompleteCommand(job)
                    .variable("EU-Ausland", Reiseziel)
                    .send()
                    .join();
        }
        else {
        client.newCompleteCommand(job)
                .variable("Nicht-EU-Ausland", Reiseziel)
                .send()
                .join();
        }
    }
}
