package doctor.backend.dto.prescription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionResponse {

    private Long id;

    // =========================
    // Patient Information
    // =========================

    private Long patientId;
    private String patientName;
    private String species;
    private String breed;
    private String gender;
    private String icon;

    // =========================
    // Owner Information
    // =========================

    private Long ownerId;
    private String ownerName;
    private String ownerPhone;

    // =========================
    // Medical Record
    // =========================

    private Long medicalRecordId;

    // =========================
    // Prescription Information
    // =========================

    private LocalDate prescriptionDate;

    private String diagnosis;

    private String instructions;

    private String notes;

    private String doctorName;
    private Long doctorId;

    // =========================
    // Prescription Items
    // =========================

    private List<PrescriptionItemResponse> items;

    // =========================
    // Timestamps
    // =========================

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
    // Patient
    // =========================

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // =========================
    // Owner
    // =========================

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

    // =========================
    // Medical Record
    // =========================

    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    // =========================
    // Prescription Date
    // =========================

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    // =========================
    // Diagnosis
    // =========================

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    // =========================
    // Instructions
    // =========================

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    // =========================
    // Doctor
    // =========================

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // =========================
    // Items
    // =========================

    public List<PrescriptionItemResponse> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemResponse> items) {
        this.items = items;
    }

    // =========================
    // Timestamps
    // =========================

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