package de.viadee.bpm.camunda.travelinsuranceprocessapp.service;

import java.util.Base64;

public class Authentication {

    public static String getBasicAuthenticationHeader() {
        String valueToEncode = "user3" + ":" + "SHKw24Ti";
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
