package doctor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_logs")
public class SmsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Phone Number
    // =========================

    @Column(nullable = false)
    private String phoneNumber;

    // =========================
    // Message
    // =========================

    @Column(columnDefinition = "TEXT")
    private String message;

    // =========================
    // Message Type
    // =========================

    private String type;

    // =========================
    // Status
    // =========================

    private String status;

    // =========================
    // Provider
    // =========================

    private String provider;

    // =========================
    // Provider Message ID
    // =========================

    private String providerMessageId;

    // =========================
    // Error Message
    // =========================

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // =========================
    // Created At
    // =========================

    private LocalDateTime createdAt;

    // =========================
    // Updated At
    // =========================

    private LocalDateTime updatedAt;

    // =========================
    // Create
    // =========================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null || status.isBlank()) {
            status = "Pending";
        }

        if (type == null || type.isBlank()) {
            type = "General";
        }
    }

    // =========================
    // Update
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public void setProviderMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}