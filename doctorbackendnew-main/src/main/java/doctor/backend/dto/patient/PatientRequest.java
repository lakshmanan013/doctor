package doctor.backend.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class PatientRequest {

    // =====================================================
    // PATIENT NAME
    // =====================================================

    @NotBlank(message = "Patient name is required")
    private String name;

    // =====================================================
    // SPECIES
    // =====================================================

    @NotBlank(message = "Species is required")
    private String species;

    // =====================================================
    // BREED
    // =====================================================

    private String breed;

    // =====================================================
    // GENDER
    // =====================================================

    private String gender;

    // =====================================================
    // DATE OF BIRTH
    // =====================================================

    private LocalDate dateOfBirth;

    // =====================================================
    // WEIGHT
    // =====================================================

    @PositiveOrZero(message = "Weight cannot be negative")
    private Double weight;

    // =====================================================
    // COLOR
    // =====================================================

    private String color;

    // =====================================================
    // MICROCHIP
    // =====================================================

    private String microchipNumber;

    // =====================================================
    // MEDICAL ALERTS
    // =====================================================

    private String medicalAlerts;

    // =====================================================
    // STATUS
    // =====================================================

    private String status;

    // =====================================================
    // NOTES
    // =====================================================

    private String notes;

    // =====================================================
    // ICON
    // =====================================================

    private String icon;

    // =====================================================
    // OWNER
    // =====================================================

    @NotNull(message = "Owner is required")
    private Long ownerId;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PatientRequest() {
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getMedicalAlerts() {
        return medicalAlerts;
    }

    public void setMedicalAlerts(String medicalAlerts) {
        this.medicalAlerts = medicalAlerts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}