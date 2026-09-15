package doctor.backend.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequest {

    private Long invoiceId;

    private Long ownerId;

    private Long patientId;

    private BigDecimal amount;

    private String paymentMethod;

    private String transactionId;

    private String referenceNumber;

    private LocalDateTime paymentDate;

    private String notes;

    // =========================
    // Invoice ID
    // =========================

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    // =========================
    // Owner ID
    // =========================

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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
    // Amount
    // =========================

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // =========================
    // Payment Method
    // =========================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // =========================
    // Transaction ID
    // =========================

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // =========================
    // Reference Number
    // =========================

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    // =========================
    // Payment Date
    // =========================

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    // =========================
    // Notes
    // =========================

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}