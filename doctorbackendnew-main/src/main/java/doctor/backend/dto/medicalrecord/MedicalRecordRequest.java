package doctor.backend.dto.medicalrecord;

import java.time.LocalDate;

public class MedicalRecordRequest {

    private Long patientId;

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
}