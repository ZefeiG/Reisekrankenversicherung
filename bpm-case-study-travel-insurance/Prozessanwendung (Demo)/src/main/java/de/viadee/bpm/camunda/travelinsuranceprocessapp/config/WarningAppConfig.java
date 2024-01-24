package de.viadee.bpm.camunda.travelinsuranceprocessapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WarningAppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
