package com.zenve.admin.service;

import com.zenve.admin.config.DatabaseProperties;
import com.zenve.admin.dto.DatabaseInfoResponse;
import com.zenve.admin.repository.AdminRepository;
import com.zenve.admin.repository.DoctorRepository;
import com.zenve.admin.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DatabaseInfoService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInfoService.class);

    private final DataSource dataSource;
    private final DatabaseProperties databaseProperties;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final NotificationRepository notificationRepository;

    public DatabaseInfoService(DataSource dataSource,
                                DatabaseProperties databaseProperties,
                                DoctorRepository doctorRepository,
                                AdminRepository adminRepository,
                                NotificationRepository notificationRepository) {
        this.dataSource = dataSource;
        this.databaseProperties = databaseProperties;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.notificationRepository = notificationRepository;
    }

    public DatabaseInfoResponse getInfo() {
        boolean connected;
        String error = null;

        try (Connection connection = dataSource.getConnection()) {
            connected = connection.isValid(2);
        } catch (SQLException e) {
            log.warn("Database connectivity check failed: {}", e.getMessage());
            connected = false;
            error = e.getMessage();
        }

        boolean isMysql = "mysql".equalsIgnoreCase(databaseProperties.driver());
        Long sizeBytes = null;
        String lastModified = null;

        if (!isMysql) {
            Path dbFile = Path.of(databaseProperties.filePath() + ".mv.db");
            try {
                if (Files.exists(dbFile)) {
                    sizeBytes = Files.size(dbFile);
                    lastModified = Files.getLastModifiedTime(dbFile).toInstant().toString();
                }
            } catch (Exception e) {
                log.warn("Could not read database file metadata: {}", e.getMessage());
            }
        }

        DatabaseInfoResponse.DbCounts counts = new DatabaseInfoResponse.DbCounts(
                doctorRepository.count(),
                adminRepository.count(),
                notificationRepository.count()
        );

        return new DatabaseInfoResponse(
                connected,
                databaseProperties.driver(),
                databaseProperties.name(),
                isMysql ? "MySQL" : "H2 (embedded file database)",
                databaseProperties.host(),
                sizeBytes,
                lastModified,
                error,
                counts
        );
    }
}
