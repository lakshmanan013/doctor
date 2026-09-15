package doctor.backend.repository;

import doctor.backend.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VaccinationRepository
        extends JpaRepository<Vaccination, Long> {

    // Get all vaccinations for a patient
    List<Vaccination> findByPatientId(Long patientId);

    // Get vaccinations by status
    List<Vaccination> findByStatus(String status);

    // Get vaccinations by vaccine name
    List<Vaccination> findByVaccineName(String vaccineName);

    // Get vaccinations due before a specific date
    List<Vaccination> findByNextDueDateBefore(
            LocalDate date
    );

    // Get vaccinations due between two dates
    List<Vaccination> findByNextDueDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Get patient vaccinations by status
    List<Vaccination> findByPatientIdAndStatus(
            Long patientId,
            String status
    );

    // Get vaccinations on a particular date
    List<Vaccination> findByVaccinationDate(
            LocalDate vaccinationDate
    );

    // Doctor-specific methods
    List<Vaccination> findByDoctorId(Long doctorId);

    Optional<Vaccination> findByIdAndDoctorId(Long id, Long doctorId);

    List<Vaccination> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<Vaccination> findByStatusAndDoctorId(String status, Long doctorId);

    List<Vaccination> findByVaccineNameAndDoctorId(String vaccineName, Long doctorId);

    List<Vaccination> findByNextDueDateBeforeAndDoctorId(LocalDate date, Long doctorId);

    List<Vaccination> findByNextDueDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    List<Vaccination> findByPatientIdAndStatusAndDoctorId(
            Long patientId,
            String status,
            Long doctorId
    );

    List<Vaccination> findByVaccinationDateAndDoctorId(
            LocalDate vaccinationDate,
            Long doctorId
    );

    long countByDoctorId(Long doctorId);

    List<Vaccination> findByDoctorIdIsNull();
}