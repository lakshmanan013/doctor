package doctor.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Invoice Number
    // =========================

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

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
    // Invoice Date
    // =========================

    private LocalDate invoiceDate;

    // =========================
    // Due Date
    // =========================

    private LocalDate dueDate;

    // =========================
    // Amounts
    // =========================

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal tax;

    @Column(precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal dueAmount;

    // =========================
    // Status
    // =========================

    private String status;

    // =========================
    // Payment Status
    // =========================

    private String paymentStatus;

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

        if (invoiceDate == null) {
            invoiceDate = LocalDate.now();
        }

        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }

        if (paymentStatus == null ||
                paymentStatus.isBlank()) {

            paymentStatus = "UNPAID";
        }

        if (subtotal == null) {
            subtotal = BigDecimal.ZERO;
        }

        if (tax == null) {
            tax = BigDecimal.ZERO;
        }

        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        if (dueAmount == null) {
            dueAmount = totalAmount;
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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