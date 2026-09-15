package doctor.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    private String manufacturer;

    private String description;

    private String dosageForm;

    private String strength;

    private String unit;

    private Double price;

    private Integer stockQuantity;

    private Integer reorderLevel;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // CREATE
    // =========================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // =========================
    // UPDATE
    // =========================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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
    // NAME
    // =========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================
    // CATEGORY
    // =========================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // =========================
    // MANUFACTURER
    // =========================

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    // =========================
    // DESCRIPTION
    // =========================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // =========================
    // DOSAGE FORM
    // =========================

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    // =========================
    // STRENGTH
    // =========================

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    // =========================
    // UNIT
    // =========================

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // =========================
    // PRICE
    // =========================

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // =========================
    // STOCK
    // =========================

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    // =========================
    // REORDER LEVEL
    // =========================

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    // =========================
    // STATUS
    // =========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================
    // CREATED AT
    // =========================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =========================
    // UPDATED AT
    // =========================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}