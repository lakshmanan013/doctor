package doctor.backend.dto.inventory;

import java.time.LocalDate;

public class MedicineBatchRequest {

    private Long medicineId;

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

    public void setManufacturingDate(LocalDate manufacturingDate) {
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

    public void setRemainingQuantity(Integer remainingQuantity) {
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
}