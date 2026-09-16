package com.zenve.admin.service;

import com.zenve.admin.config.ZippyCrmProperties;
import com.zenve.admin.model.Doctor;
import com.zenve.admin.model.DoctorStatus;
import com.zenve.admin.repository.DoctorRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

/**
 * Synchronizes doctors from Zenve Doctor/Admin portal to Zippy CRM (FastAPI +
 * MySQL pet_management.doctors).
 * Keeps Zippy CRM's Doctors page in sync when doctors are created, registered,
 * approved, or rejected.
 */
@Service
public class ZippyCrmSyncService {

    private static final Logger log = LoggerFactory.getLogger(ZippyCrmSyncService.class);

    private final ZippyCrmProperties properties;
    private final DoctorRepository doctorRepository;
    private final DoctorAppNotifier doctorAppNotifier;

    public ZippyCrmSyncService(ZippyCrmProperties properties, DoctorRepository doctorRepository,
            DoctorAppNotifier doctorAppNotifier) {
        this.properties = properties;
        this.doctorRepository = doctorRepository;
        this.doctorAppNotifier = doctorAppNotifier;
    }

    @PostConstruct
    public void initSync() {
        if (!properties.enabled()) {
            log.info("Zippy CRM sync is disabled.");
            return;
        }
        try {
            List<Doctor> allDoctors = doctorRepository.findAll();
            log.info("Starting initial sync of {} doctor(s) to Zippy CRM...", allDoctors.size());
            for (Doctor doc : allDoctors) {
                syncDoctor(doc);
            }
            log.info("Initial sync to Zippy CRM completed successfully.");
        } catch (Exception e) {
            log.warn("Could not perform initial sync to Zippy CRM: {}", e.getMessage());
        }
    }

    public void syncDoctor(Doctor doctor) {
        if (!properties.enabled() || doctor == null) {
            return;
        }

        String statusStr = mapStatusToZippy(doctor.getStatus());
        syncDirectToDatabase(doctor, statusStr);
    }

    public void syncDoctorApproved(Doctor doctor) {
        if (!properties.enabled() || doctor == null) {
            return;
        }
        syncDirectToDatabase(doctor, "approved");
    }

    public void syncDoctorRejected(Doctor doctor) {
        if (!properties.enabled() || doctor == null) {
            return;
        }
        syncDirectToDatabase(doctor, "rejected");
    }

    private String mapStatusToZippy(DoctorStatus status) {
        if (status == null)
            return "pending";
        return switch (status) {
            case approved -> "approved";
            case rejected -> "rejected";
            case pending -> "pending";
        };
    }

    /**
     * Direct sync to MySQL pet_management.doctors table with full deduplication.
     * Links qualification, speciality (specializations), and consultation fee from
     * doctor backend.
     */
    private synchronized void syncDirectToDatabase(Doctor doctor, String verificationStatus) {
        try (Connection conn = DriverManager.getConnection(
                properties.dbUrl(),
                properties.dbUser(),
                properties.dbPassword())) {

            String phone = (doctor.getPhone() != null) ? doctor.getPhone().trim() : "";
            String name = (doctor.getFullName() != null && !doctor.getFullName().isBlank())
                    ? doctor.getFullName().trim()
                    : "Dr. Unknown";

            String qualification = (doctor.getQualification() != null && !doctor.getQualification().isBlank())
                    ? doctor.getQualification().trim()
                    : "BVSc & AH";

            String specializations = (doctor.getClinicName() != null && !doctor.getClinicName().isBlank())
                    ? doctor.getClinicName().trim()
                    : "Veterinarian";

            String city = "Bangalore";
            String pincode = "560001";
            int experienceYears = 5;
            double consultationFee = 500.0;
            boolean isActive = !"rejected".equalsIgnoreCase(verificationStatus);

            String digitsOnly = phone.replaceAll("[^0-9]", "");
            String last10 = digitsOnly.length() >= 10 ? digitsOnly.substring(digitsOnly.length() - 10) : digitsOnly;

            // Check if doctor exists in pet_management.doctors by phone, 10-digit suffix,
            // or name
            String selectSql = "SELECT id FROM doctors WHERE (phone = ? AND phone != '') OR (phone LIKE ? AND ? != '') OR name = ? ORDER BY id ASC LIMIT 1";
            Integer existingId = null;

            try (PreparedStatement checkStmt = conn.prepareStatement(selectSql)) {
                checkStmt.setString(1, phone);
                checkStmt.setString(2, "%" + last10);
                checkStmt.setString(3, last10);
                checkStmt.setString(4, name);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("id");
                    }
                }
            }

            if (existingId != null) {
                // Update existing record (never duplicate!)
                String updateSql = "UPDATE doctors SET name = ?, qualification = ?, specializations = ?, phone = ?, " +
                        "city = ?, pincode = ?, experience_years = ?, consultation_fee = ?, verification_status = ?, is_active = ? "
                        +
                        "WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, qualification);
                    updateStmt.setString(3, specializations);
                    updateStmt.setString(4, phone);
                    updateStmt.setString(5, city);
                    updateStmt.setString(6, pincode);
                    updateStmt.setInt(7, experienceYears);
                    updateStmt.setDouble(8, consultationFee);
                    updateStmt.setString(9, verificationStatus);
                    updateStmt.setBoolean(10, isActive);
                    updateStmt.setInt(11, existingId);
                    updateStmt.executeUpdate();
                    log.info("Updated doctor in Zippy CRM (ID: {}): {} [status: {}]", existingId, name,
                            verificationStatus);
                }
            } else {
                // Insert new record
                String insertSql = "INSERT INTO doctors (name, qualification, specializations, phone, city, pincode, " +
                        "experience_years, consultation_fee, verification_status, is_active) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, name);
                    insertStmt.setString(2, qualification);
                    insertStmt.setString(3, specializations);
                    insertStmt.setString(4, phone);
                    insertStmt.setString(5, city);
                    insertStmt.setString(6, pincode);
                    insertStmt.setInt(7, experienceYears);
                    insertStmt.setDouble(8, consultationFee);
                    insertStmt.setString(9, verificationStatus);
                    insertStmt.setBoolean(10, isActive);
                    insertStmt.executeUpdate();
                    log.info("Created new doctor in Zippy CRM: {} [status: {}]", name, verificationStatus);
                }
            }
        } catch (SQLException e) {
            log.warn("Direct DB sync to Zippy CRM pet_management failed for {}: {}", doctor.getFullName(),
                    e.getMessage());
        }
    }
}
