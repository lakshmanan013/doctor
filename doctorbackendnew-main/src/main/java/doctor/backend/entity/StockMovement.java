package doctor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Medicine
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    // =========================
    // Batch
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private MedicineBatch batch;

    // =========================
    // Movement Information
    // =========================

    @Column(nullable = false)
    private String movementType;
    /*
        PURCHASE
        SALE
        PRESCRIPTION
        RETURN
        ADJUSTMENT
        EXPIRED
        DAMAGED
    */

    @Column(nullable = false)
    private Integer quantity;

    private Integer previousStock;

    private Integer newStock;

    // =========================
    // Reference
    // =========================

    private String referenceType;

    private Long referenceId;

    // =========================
    // Reason
    // =========================

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String performedBy;

    // =========================
    // Timestamp
    // =========================

    private LocalDateTime createdAt;

    // =========================
    // Pre Persist
    // =========================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public MedicineBatch getBatch() {
        return batch;
    }

    public void setBatch(MedicineBatch batch) {
        this.batch = batch;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(Integer previousStock) {
        this.previousStock = previousStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}