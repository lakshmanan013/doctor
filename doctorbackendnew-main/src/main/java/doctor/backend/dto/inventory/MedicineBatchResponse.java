package doctor.backend.dto.inventory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicineBatchResponse {

    private Long id;

    private Long medicineId;

    private String medicineName;

    private String batchNumber;

    private LocalDate manufacturingDate;

    private LocalDate expiryDate;

    private Integer quantity;

    private Integer remainingQuantity;

    private Double purchasePrice;

    private Double sellingPrice;

    private String supplierName;

    private String supplierContact;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
    // Batch Number
    // =========================

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    // =========================
    // Manufacturing Date
    // =========================

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(
            LocalDate manufacturingDate) {

        this.manufacturingDate = manufacturingDate;
    }

    // =========================
    // Expiry Date
    // =========================

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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
    // Remaining Quantity
    // =========================

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(
            Integer remainingQuantity) {

        this.remainingQuantity = remainingQuantity;
    }

    // =========================
    // Purchase Price
    // =========================

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    // =========================
    // Selling Price
    // =========================

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    // =========================
    // Supplier Name
    // =========================

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    // =========================
    // Supplier Contact
    // =========================

    public String getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    // =========================
    // Status
    // =========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // =========================
    // Updated At
    // =========================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}