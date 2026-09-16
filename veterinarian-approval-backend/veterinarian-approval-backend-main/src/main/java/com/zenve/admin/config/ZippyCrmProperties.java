package com.zenve.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.zippy-crm")
public record ZippyCrmProperties(
        boolean enabled,
        String baseUrl,
        String dbUrl,
        String dbUser,
        String dbPassword
) {
    public ZippyCrmProperties {
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:mysql://localhost:3306/pet_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
        }
        if (dbUser == null || dbUser.isBlank()) {
            dbUser = "root";
        }
        if (dbPassword == null || dbPassword.isBlank()) {
            dbPassword = "NewPassword@123";
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://127.0.0.1:8000";
        }
    }
}
