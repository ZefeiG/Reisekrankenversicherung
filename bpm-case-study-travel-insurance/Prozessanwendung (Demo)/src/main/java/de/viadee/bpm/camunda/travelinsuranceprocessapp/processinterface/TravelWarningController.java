package de.viadee.bpm.camunda.travelinsuranceprocessapp.processinterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.worker.TravelWarningService;

@RestController
public class TravelWarningController {

    @Autowired
    private TravelWarningService travelWarningService;

    @GetMapping("/travel-warnings")
    public String getTravelWarnings() {
        return travelWarningService.getTravelWarnings();
    }
}
