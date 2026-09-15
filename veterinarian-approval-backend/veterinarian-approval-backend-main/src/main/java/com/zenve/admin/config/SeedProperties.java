package com.zenve.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(String adminName, String adminEmail, String adminPassword) {
}
