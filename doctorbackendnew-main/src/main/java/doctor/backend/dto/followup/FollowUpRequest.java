package doctor.backend.dto.followup;

import java.time.LocalDate;

public class FollowUpRequest {

    private Long patientId;

    private Long appointmentId;

    private LocalDate followUpDate;

    private LocalDate nextFollowUpDate;

    private String reason;

    private String status;

    private String symptoms;

    private String findings;

    private String treatment;

    private String recommendations;

    private String notes;

    private String doctorName;

    private Boolean reminderSent;

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
    // Appointment ID
    // =========================

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    // =========================
    // Follow-Up Date
    // =========================

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    // =========================
    // Next Follow-Up Date
    // =========================

    public LocalDate getNextFollowUpDate() {
        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }

    // =========================
    // Reason
    // =========================

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
    // Symptoms
    // =========================

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    // =========================
    // Findings
    // =========================

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
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
    // Recommendations
    // =========================

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
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
    // Doctor Name
    // =========================

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // =========================
    // Reminder Sent
    // =========================

    public Boolean getReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }
}