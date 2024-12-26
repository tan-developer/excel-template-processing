package com.excel.poiAndJxls.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.security.GeneralSecurityException;

@Configuration
public class RestTemplateConfig {

    @Value("${superset.baseUrl}")
    private String BASE_URL;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.customizers(restTemplate -> {
            DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
            restTemplate.setUriTemplateHandler(factory);
        }).build();
    }
}
