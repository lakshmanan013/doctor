package doctor.backend.dto.inventory;

import java.time.LocalDateTime;

public class StockMovementResponse {

    private Long id;

    private Long medicineId;

    private String medicineName;

    private Long batchId;

    private String batchNumber;

    private String movementType;

    private Integer quantity;

    private Integer previousStock;

    private Integer newStock;

    private String referenceType;

    private Long referenceId;

    private String reason;

    private String performedBy;

    private LocalDateTime createdAt;

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
    // Medicine ID
    // =========================

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    // =========================
    // Medicine Name
    // =========================

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    // =========================
    // Batch ID
    // =========================

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    // =========================
    // Batch Number
    // =========================

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    // =========================
    // Movement Type
    // =========================

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    // =========================
    // Quantity
    // =========================

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // =========================
    // Previous Stock
    // =========================

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    // =========================
    // New Stock
    // =========================

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    // =========================
    // Reference Type
    // =========================

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    // =========================
    // Reference ID
    // =========================

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    // =========================
    // Reason
    // =========================

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // =========================
    // Performed By
    // =========================

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
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
}