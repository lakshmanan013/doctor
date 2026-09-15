package doctor.backend.service;

import doctor.backend.dto.notification.NotificationRequest;
import doctor.backend.dto.notification.NotificationResponse;
import doctor.backend.entity.Appointment;
import doctor.backend.entity.Notification;
import doctor.backend.entity.Patient;
import doctor.backend.repository.AppointmentRepository;
import doctor.backend.repository.NotificationRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final CurrentUserProvider currentUserProvider;

    public NotificationService(
            NotificationRepository notificationRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            CurrentUserProvider currentUserProvider) {

        this.notificationRepository = notificationRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE NOTIFICATION
    // =====================================================

    public NotificationResponse createNotification(
            NotificationRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (request.getTitle() == null ||
                request.getTitle().trim().isEmpty()) {

            throw new RuntimeException(
                    "Notification title is required"
            );
        }

        Notification notification = new Notification();

        notification.setTitle(
                request.getTitle()
        );

        notification.setMessage(
                request.getMessage()
        );

        notification.setType(
                request.getType() != null &&
                        !request.getType().trim().isEmpty()
                        ? request.getType()
                        : "General"
        );

        notification.setUserId(
                request.getUserId() != null ? request.getUserId() : doctorId
        );

        notification.setPriority(
                request.getPriority() != null &&
                        !request.getPriority().trim().isEmpty()
                        ? request.getPriority()
                        : "Normal"
        );

        if (request.getRead() != null) {
            notification.setRead(
                    request.getRead()
            );
        }

        // =========================
        // Patient
        // =========================

        if (request.getPatientId() != null) {

            Patient patient =
                    patientRepository
                            .findByIdAndDoctorId(request.getPatientId(), doctorId)
                            .orElse(null);

            notification.setPatient(patient);
        }

        // =========================
        // Appointment
        // =========================

        if (request.getAppointmentId() != null) {

            Appointment appointment =
                    appointmentRepository
                            .findByIdAndDoctorId(request.getAppointmentId(), doctorId)
                            .orElse(null);

            notification.setAppointment(
                    appointment
            );
        }

        Notification saved =
                notificationRepository.save(
                        notification
                );

        return mapToResponse(saved);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository.findByUserId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(
            Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(notification);
    }

    // =====================================================
    // GET BY USER
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUser(
            Long userId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET UNREAD
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnread() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserIdAndReadFalse(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET UNREAD BY USER
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadByUser(
            Long userId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserIdAndReadFalse(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET READ
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getRead() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserIdAndReadTrue(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY TYPE
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByType(
            String type) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserIdAndType(doctorId, type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PRIORITY
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByPriority(
            String priority) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByUserId(doctorId)
                .stream()
                .filter(n -> priority.equalsIgnoreCase(n.getPriority()))
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PATIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByPatient(
            Long patientId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByPatientId(patientId)
                .stream()
                .filter(n -> doctorId.equals(n.getUserId()))
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY APPOINTMENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByAppointment(
            Long appointmentId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return notificationRepository
                .findByAppointmentId(appointmentId)
                .stream()
                .filter(n -> doctorId.equals(n.getUserId()))
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // MARK AS READ
    // =====================================================

    public NotificationResponse markAsRead(
            Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        notification.setRead(true);

        Notification updated =
                notificationRepository.save(
                        notification
                );

        return mapToResponse(updated);
    }

    // =====================================================
    // MARK AS UNREAD
    // =====================================================

    public NotificationResponse markAsUnread(
            Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: "
                                                + id
                                )
                        );

        notification.setRead(false);

        Notification updated =
                notificationRepository.save(
                        notification
                );

        return mapToResponse(updated);
    }

    // =====================================================
    // MARK ALL USER NOTIFICATIONS AS READ
    // =====================================================

    public void markAllAsRead(Long userId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndReadFalse(doctorId);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(
                notifications
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteNotification(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Notification notification = notificationRepository.findByIdAndUserId(id, doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found with id: "
                                        + id
                        )
                );

        notificationRepository.delete(notification);
    }

    // =====================================================
    // DELETE ALL USER NOTIFICATIONS
    // =====================================================

    public void deleteUserNotifications(
            Long userId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        notificationRepository.deleteByUserId(
                doctorId
        );
    }

    // =====================================================
    // MAP ENTITY TO RESPONSE
    // =====================================================

    private NotificationResponse mapToResponse(
            Notification notification) {

        NotificationResponse response =
                new NotificationResponse();

        response.setId(
                notification.getId()
        );

        response.setTitle(
                notification.getTitle()
        );

        response.setMessage(
                notification.getMessage()
        );

        response.setType(
                notification.getType()
        );

        response.setUserId(
                notification.getUserId()
        );

        response.setPriority(
                notification.getPriority()
        );

        response.setRead(
                notification.isRead()
        );

        // =========================
        // Patient
        // =========================

        if (notification.getPatient() != null) {

            response.setPatientId(
                    notification.getPatient().getId()
            );
        }

        // =========================
        // Appointment
        // =========================

        if (notification.getAppointment() != null) {

            response.setAppointmentId(
                    notification
                            .getAppointment()
                            .getId()
            );
        }

        response.setCreatedAt(
                notification.getCreatedAt()
        );

        response.setUpdatedAt(
                notification.getUpdatedAt()
        );

        return response;
    }
}