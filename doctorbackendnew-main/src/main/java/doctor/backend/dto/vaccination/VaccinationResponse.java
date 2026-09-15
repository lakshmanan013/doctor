package doctor.backend.dto.vaccination;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VaccinationResponse {

    private Long id;

    private Long patientId;

    private String patientName;

    private String vaccineName;

    private String vaccineType;

    private String manufacturer;

    private String batchNumber;

    private LocalDate vaccinationDate;

    private LocalDate nextDueDate;

    private String dosage;

    private String route;

    private String administeredBy;
    private Long doctorId;

    private String status;

    private String notes;

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
    // Patient ID
    // =========================

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    // =========================
    // Patient Name
    // =========================

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // =========================
    // Vaccine Name
    // =========================

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    // =========================
    // Vaccine Type
    // =========================

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    // =========================
    // Manufacturer
    // =========================

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
    // Vaccination Date
    // =========================

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(LocalDate vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    // =========================
    // Next Due Date
    // =========================

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    // =========================
    // Dosage
    // =========================

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    // =========================
    // Route
    // =========================

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    // =========================
    // Administered By
    // =========================

    public String getAdministeredBy() {
        return administeredBy;
    }

    public void setAdministeredBy(String administeredBy) {
        this.administeredBy = administeredBy;
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
    // Notes
    // =========================

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}