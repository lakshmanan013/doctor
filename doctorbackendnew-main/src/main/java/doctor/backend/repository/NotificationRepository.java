package doctor.backend.repository;

import doctor.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get notifications for a user
    List<Notification> findByUserId(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    // Get unread notifications
    List<Notification> findByReadFalse();

    // Get read notifications
    List<Notification> findByReadTrue();

    // Get unread notifications for a user
    List<Notification> findByUserIdAndReadFalse(Long userId);

    // Get read notifications for a user
    List<Notification> findByUserIdAndReadTrue(Long userId);

    // Get notifications by type for a user
    List<Notification> findByUserIdAndType(Long userId, String type);

    // Get notifications by type
    List<Notification> findByType(String type);

    // Get notifications by priority
    List<Notification> findByPriority(String priority);

    // Get notifications related to a patient
    List<Notification> findByPatientId(Long patientId);

    // Get notifications related to an appointment
    List<Notification> findByAppointmentId(Long appointmentId);

    // Delete notifications for a user
    void deleteByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndReadFalse(Long userId);
}