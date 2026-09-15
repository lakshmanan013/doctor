package doctor.backend.repository;

import doctor.backend.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowUpRepository
        extends JpaRepository<FollowUp, Long> {

    List<FollowUp> findByPatientId(Long patientId);

    List<FollowUp> findByStatus(String status);

    List<FollowUp> findByPatientIdAndStatus(
            Long patientId,
            String status
    );

    List<FollowUp> findByFollowUpDate(
            LocalDate followUpDate
    );

    List<FollowUp> findByFollowUpDateBefore(
            LocalDate date
    );

    List<FollowUp> findByFollowUpDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<FollowUp> findByNextFollowUpDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<FollowUp> findByNextFollowUpDateBefore(
            LocalDate date
    );

    List<FollowUp> findByAppointmentId(
            Long appointmentId
    );

    List<FollowUp> findByReminderSentFalse();

    // Doctor-specific methods
    List<FollowUp> findByDoctorId(Long doctorId);

    Optional<FollowUp> findByIdAndDoctorId(Long id, Long doctorId);

    List<FollowUp> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<FollowUp> findByStatusAndDoctorId(String status, Long doctorId);

    List<FollowUp> findByPatientIdAndStatusAndDoctorId(
            Long patientId,
            String status,
            Long doctorId
    );

    List<FollowUp> findByFollowUpDateAndDoctorId(
            LocalDate followUpDate,
            Long doctorId
    );

    List<FollowUp> findByFollowUpDateBeforeAndDoctorId(
            LocalDate date,
            Long doctorId
    );

    List<FollowUp> findByFollowUpDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    List<FollowUp> findByNextFollowUpDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    List<FollowUp> findByNextFollowUpDateBeforeAndDoctorId(
            LocalDate date,
            Long doctorId
    );

    List<FollowUp> findByAppointmentIdAndDoctorId(
            Long appointmentId,
            Long doctorId
    );

    List<FollowUp> findByReminderSentFalseAndDoctorId(Long doctorId);

    long countByDoctorId(Long doctorId);

    List<FollowUp> findByDoctorIdIsNull();
}