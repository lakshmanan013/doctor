package doctor.backend.repository;

import doctor.backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    // Get all prescriptions for a patient
    List<Prescription> findByPatientId(Long patientId);

    // Get prescriptions by medical record
    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    // Get prescriptions by date
    List<Prescription> findByPrescriptionDate(LocalDate prescriptionDate);

    // Get patient prescriptions by date
    List<Prescription> findByPatientIdAndPrescriptionDate(
            Long patientId,
            LocalDate prescriptionDate
    );

    // Get prescriptions between dates
    List<Prescription> findByPrescriptionDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Doctor-specific methods
    List<Prescription> findByDoctorId(Long doctorId);

    Optional<Prescription> findByIdAndDoctorId(Long id, Long doctorId);

    List<Prescription> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<Prescription> findByMedicalRecordIdAndDoctorId(Long medicalRecordId, Long doctorId);

    List<Prescription> findByPrescriptionDateAndDoctorId(LocalDate prescriptionDate, Long doctorId);

    List<Prescription> findByPatientIdAndPrescriptionDateAndDoctorId(
            Long patientId,
            LocalDate prescriptionDate,
            Long doctorId
    );

    List<Prescription> findByPrescriptionDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    long countByDoctorId(Long doctorId);

    List<Prescription> findByDoctorIdIsNull();
}