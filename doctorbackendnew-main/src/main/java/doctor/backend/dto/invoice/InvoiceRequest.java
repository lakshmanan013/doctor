package doctor.backend.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceRequest {

    private Long ownerId;

    private Long patientId;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal discount;

    private BigDecimal paidAmount;

    private String notes;

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
    // Invoice Date
    // =========================

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    // =========================
    // Due Date
    // =========================

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // =========================
    // Subtotal
    // =========================

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // =========================
    // Tax
    // =========================

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    // =========================
    // Discount
    // =========================

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    // =========================
    // Paid Amount
    // =========================

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
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