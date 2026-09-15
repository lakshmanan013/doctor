package com.zenve.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * driver: "mysql" or "h2" — set per Spring profile in application.yml (selected via
 *         the DB_DRIVER env var, defaulting to "h2" for a zero-setup local run).
 * host:   for mysql, host:port; for h2, the on-disk file path (used by the frontend's
 *         Database page as "connection details").
 * name:   the logical database name shown in the UI (defaults to "vetcare").
 * filePath: only used for h2, the base path passed to jdbc:h2:file:<filePath>.
 */
@ConfigurationProperties(prefix = "app.database")
public record DatabaseProperties(String driver, String host, String name, String filePath) {
}
