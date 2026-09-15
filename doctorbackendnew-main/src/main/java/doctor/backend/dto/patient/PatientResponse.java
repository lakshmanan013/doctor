package doctor.backend.dto.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientResponse {

    private Long id;

    private String petId;

    private String name;

    private String species;

    private String breed;

    private String gender;

    private LocalDate dateOfBirth;

    private Double weight;

    private String color;

    private String microchipNumber;

    private String medicalAlerts;

    private String status;

    private String notes;

    private String icon;

    private Long ownerId;

    private Long doctorId;

    private String ownerName;

    private String ownerPhone;

    private String ownerEmail;

    private String ownerAddress;

    private String ownerCity;

    private String ownerState;

    private String ownerPincode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =====================================================
    // ID
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =====================================================
    // PET ID
    // =====================================================

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    // =====================================================
    // PATIENT
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

    // =====================================================
    // OWNER
    // =====================================================

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getOwnerCity() {
        return ownerCity;
    }

    public void setOwnerCity(String ownerCity) {
        this.ownerCity = ownerCity;
    }

    public String getOwnerState() {
        return ownerState;
    }

    public void setOwnerState(String ownerState) {
        this.ownerState = ownerState;
    }

    public String getOwnerPincode() {
        return ownerPincode;
    }

    public void setOwnerPincode(String ownerPincode) {
        this.ownerPincode = ownerPincode;
    }

    // =====================================================
    // TIMESTAMPS
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}