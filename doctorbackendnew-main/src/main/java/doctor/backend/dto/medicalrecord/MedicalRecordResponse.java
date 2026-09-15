package doctor.backend.dto.medicalrecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecordResponse {

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
    // Medical Information
    // =========================

    private LocalDate visitDate;

    private String chiefComplaint;

    private String symptoms;

    private String diagnosis;

    private String clinicalFindings;

    private String treatment;

    private Double weight;

    private Double temperature;

    private String notes;

    private String doctorName;
    private Long doctorId;

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
    // Patient Information
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
    // Owner Information
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
    // Visit Date
    // =========================

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }


    // =========================
    // Chief Complaint
    // =========================

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }


    // =========================
    // Symptoms
    // =========================

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
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
    // Clinical Findings
    // =========================

    public String getClinicalFindings() {
        return clinicalFindings;
    }

    public void setClinicalFindings(String clinicalFindings) {
        this.clinicalFindings = clinicalFindings;
    }


    // =========================
    // Treatment
    // =========================

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }


    // =========================
    // Weight
    // =========================

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }


    // =========================
    // Temperature
    // =========================

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
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