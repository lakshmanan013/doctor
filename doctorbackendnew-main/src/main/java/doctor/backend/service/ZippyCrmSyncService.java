package doctor.backend.service;

import doctor.backend.config.ZippyCrmProperties;
import doctor.backend.entity.DoctorProfile;
import doctor.backend.entity.User;
import doctor.backend.repository.DoctorProfileRepository;
import doctor.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

/**
 * Synchronizes doctors from Zenve Doctor Backend to Zippy CRM (FastAPI +
 * MySQL pet_management database).
 * Keeps Zippy CRM's Doctors page in sync when doctors are registered, approved,
 * rejected, or when they update their profile (including speciality,
 * qualification,
 * and consultation fee).
 */
@Service
public class ZippyCrmSyncService {

    private static final Logger log = LoggerFactory.getLogger(ZippyCrmSyncService.class);

    private final ZippyCrmProperties properties;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public ZippyCrmSyncService(ZippyCrmProperties properties, UserRepository userRepository,
            DoctorProfileRepository doctorProfileRepository) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @PostConstruct
    public void init() {
        if (!properties.isEnabled()) {
            log.info("Zippy CRM sync is disabled.");
            return;
        }

        try {
            List<User> doctors = userRepository.findAll().stream()
                    .filter(u -> "DOCTOR".equalsIgnoreCase(u.getRole()))
                    .toList();
            log.info("Starting initial sync of {} doctor(s) from Doctor Backend to Zippy CRM...", doctors.size());
            for (User doc : doctors) {
                syncDoctor(doc);
            }
            log.info("Initial sync to Zippy CRM completed successfully.");
        } catch (Exception e) {
            log.warn("Could not perform initial sync to Zippy CRM: {}", e.getMessage());
        }
    }

    public void syncDoctor(Long userId) {
        if (userId == null)
            return;
        userRepository.findById(userId).ifPresent(this::syncDoctor);
    }

    public void syncDoctor(DoctorProfile profile) {
        if (profile == null || profile.getUserId() == null)
            return;
        userRepository.findById(profile.getUserId()).ifPresent(user -> syncDoctor(user, profile));
    }

    public void syncDoctor(User user) {
        if (user == null || !properties.isEnabled()) {
            return;
        }
        DoctorProfile profile = doctorProfileRepository.findByUserId(user.getId()).orElse(null);
        syncDoctor(user, profile);
    }

    public void syncDoctor(User user, DoctorProfile profile) {
        if (user == null || !properties.isEnabled()) {
            return;
        }

        String approvalStatus = user.getApprovalStatus();
        String verificationStatus = mapStatusToZippy(approvalStatus);

        syncDirectToDatabase(user, profile, verificationStatus);
    }

    private String mapStatusToZippy(String approvalStatus) {
        if (approvalStatus == null) {
            return "pending";
        }
        return switch (approvalStatus.toUpperCase()) {
            case "APPROVED" -> "verified";
            case "REJECTED" -> "rejected";
            default -> "pending";
        };
    }

    /**
     * Direct sync to MySQL pet_management.doctors table with full deduplication.
     * Links qualification, speciality (specializations), phone, and consultation
     * fee from
     * doctor backend.
     */
    private synchronized void syncDirectToDatabase(User user, DoctorProfile profile, String verificationStatus) {
        try (Connection conn = DriverManager.getConnection(
                properties.getDbUrl(),
                properties.getDbUser(),
                properties.getDbPassword())) {

            String phone = (profile != null && profile.getPhone() != null && !profile.getPhone().isBlank())
                    ? profile.getPhone().trim()
                    : (user.getPhone() != null ? user.getPhone().trim() : "");

            String name = (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank())
                    ? profile.getFullName().trim()
                    : (user.getFullName() != null && !user.getFullName().isBlank() ? user.getFullName().trim()
                            : "Dr. Unknown");
            String userName = (user.getFullName() != null && !user.getFullName().isBlank()) ? user.getFullName().trim()
                    : "";
            String userPhone = (user.getPhone() != null && !user.getPhone().isBlank()) ? user.getPhone().trim() : "";

            String qualification = (profile != null && profile.getQualification() != null
                    && !profile.getQualification().isBlank())
                            ? profile.getQualification().trim()
                            : "BVSc & AH";

            String specializations = (profile != null && profile.getSpeciality() != null
                    && !profile.getSpeciality().isBlank())
                            ? profile.getSpeciality().trim()
                            : "Veterinarian";

            String city = (profile != null && profile.getCity() != null && !profile.getCity().isBlank())
                    ? profile.getCity().trim()
                    : ((profile != null && profile.getClinicHospital() != null
                            && !profile.getClinicHospital().isBlank())
                                    ? profile.getClinicHospital().trim()
                                    : "Bangalore");

            int pincode = (profile != null && profile.getPincode() != null)
                    ? profile.getPincode()
                    : 560001;

            int experienceYears = (profile != null && profile.getExperience() != null)
                    ? profile.getExperience()
                    : 5;

            double consultationFee = (profile != null && profile.getConsultationFee() != null)
                    ? profile.getConsultationFee()
                    : 500.0;
            boolean isActive = !"rejected".equalsIgnoreCase(verificationStatus);

            String digitsOnly = phone.replaceAll("[^0-9]", "");
            String last10 = digitsOnly.length() >= 10 ? digitsOnly.substring(digitsOnly.length() - 10) : digitsOnly;
            String userDigitsOnly = userPhone.replaceAll("[^0-9]", "");
            String userLast10 = userDigitsOnly.length() >= 10 ? userDigitsOnly.substring(userDigitsOnly.length() - 10)
                    : userDigitsOnly;

            // Check if doctor exists in pet_management.doctors by phone, 10-digit suffix,
            // or name
            String selectSql = "SELECT id FROM doctors WHERE (phone = ? AND phone != '') OR (phone LIKE ? AND ? != '') "
                    +
                    "OR (phone = ? AND phone != '') OR (phone LIKE ? AND ? != '') " +
                    "OR name = ? OR name = ? ORDER BY id ASC LIMIT 1";
            Integer existingId = null;

            try (PreparedStatement checkStmt = conn.prepareStatement(selectSql)) {
                checkStmt.setString(1, phone);
                checkStmt.setString(2, "%" + last10);
                checkStmt.setString(3, last10);
                checkStmt.setString(4, userPhone);
                checkStmt.setString(5, "%" + userLast10);
                checkStmt.setString(6, userLast10);
                checkStmt.setString(7, name);
                checkStmt.setString(8, userName);
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
                    updateStmt.setInt(6, pincode);
                    updateStmt.setInt(7, experienceYears);
                    updateStmt.setDouble(8, consultationFee);
                    updateStmt.setString(9, verificationStatus);
                    updateStmt.setBoolean(10, isActive);
                    updateStmt.setInt(11, existingId);
                    updateStmt.executeUpdate();
                    log.info("Updated doctor in Zippy CRM (ID: {}): {} [status: {}, speciality: {}]", existingId, name,
                            verificationStatus, specializations);
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
                    insertStmt.setInt(6, pincode);
                    insertStmt.setInt(7, experienceYears);
                    insertStmt.setDouble(8, consultationFee);
                    insertStmt.setString(9, verificationStatus);
                    insertStmt.setBoolean(10, isActive);
                    insertStmt.executeUpdate();
                    log.info("Created new doctor in Zippy CRM: {} [status: {}, speciality: {}]", name,
                            verificationStatus, specializations);
                }
            }
        } catch (SQLException e) {
            log.warn("Direct DB sync to Zippy CRM pet_management failed for {}: {}", user.getFullName(),
                    e.getMessage());
        }
    }
}
