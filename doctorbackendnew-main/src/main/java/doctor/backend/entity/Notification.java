package doctor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Notification Title
    // =========================

    @Column(nullable = false)
    private String title;

    // =========================
    // Notification Message
    // =========================

    @Column(columnDefinition = "TEXT")
    private String message;

    // =========================
    // Notification Type
    // =========================

    private String type;

    // =========================
    // Related User
    // =========================

    private Long userId;

    // =========================
    // Related Patient
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // =========================
    // Related Appointment
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // =========================
    // Read Status
    // =========================

    @Column(name = "is_read")
    private boolean read;

    // =========================
    // Priority
    // =========================

    private String priority;

    // =========================
    // Created At
    // =========================

    private LocalDateTime createdAt;

    // =========================
    // Updated At
    // =========================

    private LocalDateTime updatedAt;

    // =========================
    // Pre Persist
    // =========================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (type == null || type.isBlank()) {
            type = "General";
        }

        if (priority == null || priority.isBlank()) {
            priority = "Normal";
        }

        read = false;
    }

    // =========================
    // Pre Update
    // =========================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // =========================
    // Getters & Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}