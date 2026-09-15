package doctor.backend.dto.notification;

public class NotificationRequest {

    private String title;

    private String message;

    private String type;

    private Long userId;

    private Long patientId;

    private Long appointmentId;

    private String priority;

    private Boolean read;

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

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}