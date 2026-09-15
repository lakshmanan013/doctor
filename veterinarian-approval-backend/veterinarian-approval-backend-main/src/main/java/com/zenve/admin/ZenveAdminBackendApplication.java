package com.zenve.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZenveAdminBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZenveAdminBackendApplication.class, args);
    }
}
