package doctor.backend.dto.prescription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PrescriptionRequest {

    private Long patientId;

    private Long medicalRecordId;

    private LocalDate prescriptionDate;

    private String diagnosis;

    private String instructions;

    private String notes;

    private String doctorName;

    private BigDecimal consultationFee;

    private BigDecimal followUpFee;

    private List<PrescriptionItemRequest> items;

    // =========================
    // Patient
    // =========================

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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
    // Fees
    // =========================

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getFollowUpFee() {
        return followUpFee;
    }

    public void setFollowUpFee(BigDecimal followUpFee) {
        this.followUpFee = followUpFee;
    }

    // =========================
    // Items
    // =========================

    public List<PrescriptionItemRequest> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemRequest> items) {
        this.items = items;
    }
}