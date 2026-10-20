package com.sweetdelight.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SweetDelightApplication {

    public static void main(String[] args) {
        SpringApplication.run(SweetDelightApplication.class, args);
    }
}
