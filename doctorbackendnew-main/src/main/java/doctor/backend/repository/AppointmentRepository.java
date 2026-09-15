package doctor.backend.repository;

import doctor.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Get appointments for a particular patient
    List<Appointment> findByPatientId(Long patientId);

    // Get appointments for a particular date
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    // Get appointments by status
    List<Appointment> findByStatus(String status);

    // Get appointments by date and status
    List<Appointment> findByAppointmentDateAndStatus(
            LocalDate appointmentDate,
            String status
    );

    // Get appointments between two dates
    List<Appointment> findByAppointmentDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Get appointments for a patient on a specific date
    List<Appointment> findByPatientIdAndAppointmentDate(
            Long patientId,
            LocalDate appointmentDate
    );

    // Doctor-specific methods
    List<Appointment> findByDoctorId(Long doctorId);

    Optional<Appointment> findByIdAndDoctorId(Long id, Long doctorId);

    boolean existsByIdAndDoctorId(Long id, Long doctorId);

    List<Appointment> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<Appointment> findByAppointmentDateAndDoctorId(LocalDate appointmentDate, Long doctorId);

    List<Appointment> findByStatusAndDoctorId(String status, Long doctorId);

    List<Appointment> findByAppointmentDateAndStatusAndDoctorId(
            LocalDate appointmentDate,
            String status,
            Long doctorId
    );

    List<Appointment> findByAppointmentDateBetweenAndDoctorId(
            LocalDate startDate,
            LocalDate endDate,
            Long doctorId
    );

    List<Appointment> findByPatientIdAndAppointmentDateAndDoctorId(
            Long patientId,
            LocalDate appointmentDate,
            Long doctorId
    );

    long countByDoctorId(Long doctorId);

    long countByAppointmentDateAndDoctorId(LocalDate appointmentDate, Long doctorId);

    List<Appointment> findByDoctorIdIsNull();
}