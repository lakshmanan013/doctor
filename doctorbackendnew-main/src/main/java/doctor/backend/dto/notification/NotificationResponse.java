package doctor.backend.dto.notification;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;

    private String title;

    private String message;

    private String type;

    private Long userId;

    private Long patientId;

    private String patientName;

    private Long appointmentId;

    private String priority;

    private boolean read;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // ID
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =========================
    // Title
    // =========================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // =========================
    // Message
    // =========================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // =========================
    // Type
    // =========================

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // =========================
    // User ID
    // =========================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // =========================
    // Patient ID
    // =========================

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    // =========================
    // Patient Name
    // =========================

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // =========================
    // Appointment ID
    // =========================

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    // =========================
    // Priority
    // =========================

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    // =========================
    // Read
    // =========================

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    // =========================
    // Created At
    // =========================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // =========================
    // Updated At
    // =========================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}