package doctor.backend.dto.vaccination;

import java.time.LocalDate;

public class VaccinationRequest {

    private Long patientId;

    private String vaccineName;

    private String vaccineType;

    private String manufacturer;

    private String batchNumber;

    private LocalDate vaccinationDate;

    private LocalDate nextDueDate;

    private String dosage;

    private String route;

    private String administeredBy;

    private String status;

    private String notes;

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
}