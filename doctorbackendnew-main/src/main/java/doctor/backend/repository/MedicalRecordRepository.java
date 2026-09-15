package doctor.backend.repository;

import doctor.backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    // Get all medical records for a patient
    List<MedicalRecord> findByPatientId(Long patientId);

    // Get records for a specific date
    List<MedicalRecord> findByVisitDate(LocalDate visitDate);

    // Get records for a patient on a specific date
    List<MedicalRecord> findByPatientIdAndVisitDate(
            Long patientId,
            LocalDate visitDate
    );

    // Get records between two dates
    List<MedicalRecord> findByVisitDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Doctor-specific methods
    List<MedicalRecord> findByDoctorId(Long doctorId);

    Optional<MedicalRecord> findByIdAndDoctorId(Long id, Long doctorId);

    List<MedicalRecord> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<MedicalRecord> findByVisitDateAndDoctorId(LocalDate visitDate, Long doctorId);

    List<MedicalRecord> findByPatientIdAndVisitDateAndDoctorId(
            Long patientId,
            LocalDate visitDate,
            Long doctorId
    );

    List<MedicalRecord> findByVisitDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    long countByDoctorId(Long doctorId);

    List<MedicalRecord> findByDoctorIdIsNull();
}