package doctor.backend.dto.inventory;

public class StockMovementRequest {

    private Long medicineId;

    private Long batchId;

    private String movementType;

    private Integer quantity;

    private String referenceType;

    private Long referenceId;

    private String reason;

    private String performedBy;

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
    // Batch ID
    // =========================

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
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
}