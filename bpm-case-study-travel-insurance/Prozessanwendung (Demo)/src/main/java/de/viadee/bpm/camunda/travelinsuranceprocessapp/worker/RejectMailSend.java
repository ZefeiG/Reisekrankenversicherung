package de.viadee.bpm.camunda.travelinsuranceprocessapp.worker;

import de.viadee.bpm.camunda.travelinsuranceprocessapp.model.Partner;
import de.viadee.bpm.camunda.travelinsuranceprocessapp.service.EmailService;

public class RejectMailSend{

    public void RejectDueToTravel(Partner Partner) {
        
        if(TravelCostChecker.travelCostIsPositive && TravelStartChecker.travelStartIsInFuture && TravelEndChecker.travelStartIsBeforeEnd == false){
            String mail= Partner.getMail();
            EmailService send=new EmailService();

            String Ablehnung="Reisedaten nicht mit der Politik übereinstimmen";

            send.sendSimpleMessage(mail,Ablehnung,Ablehnung);
            
        }
    }
}