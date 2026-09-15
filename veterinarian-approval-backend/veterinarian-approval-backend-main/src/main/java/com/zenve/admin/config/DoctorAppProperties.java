package com.zenve.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.doctor-app")
public record DoctorAppProperties(String baseUrl, String internalSecret) {
}
