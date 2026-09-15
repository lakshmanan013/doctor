package doctor.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Invoice
    // =========================

    @Column(nullable = false)
    private Long invoiceId;

    // =========================
    // Owner
    // =========================

    private Long ownerId;

    // =========================
    // Patient
    // =========================

    private Long patientId;

    @Column(name = "doctor_id")
    private Long doctorId;

    // =========================
    // Amount
    // =========================

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // =========================
    // Payment Method
    // =========================

    private String paymentMethod;

    // CASH
    // UPI
    // CARD
    // BANK_TRANSFER
    // ONLINE

    // =========================
    // Payment Status
    // =========================

    private String status;

    // PENDING
    // SUCCESS
    // FAILED
    // REFUNDED

    // =========================
    // Transaction ID
    // =========================

    private String transactionId;

    // =========================
    // Reference Number
    // =========================

    private String referenceNumber;

    // =========================
    // Payment Date
    // =========================

    private LocalDateTime paymentDate;

    // =========================
    // Notes
    // =========================

    @Column(columnDefinition = "TEXT")
    private String notes;

    // =========================
    // Created / Updated
    // =========================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // Pre Persist
    // =========================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (paymentDate == null) {
            paymentDate = now;
        }

        if (status == null || status.isBlank()) {
            status = "SUCCESS";
        }

        if (paymentMethod == null ||
                paymentMethod.isBlank()) {

            paymentMethod = "CASH";
        }
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}