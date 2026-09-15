package doctor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Patient
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // =========================
    // Medical Record
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    // =========================
    // Prescription Information
    // =========================

    @Column(nullable = false)
    private LocalDate prescriptionDate;

    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String doctorName;

    @Column(name = "doctor_id")
    private Long doctorId;

    // =========================
    // Prescription Items
    // =========================

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PrescriptionItem> items = new ArrayList<>();

    // =========================
    // Timestamps
    // =========================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // Pre Persist
    // =========================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // =========================
    // Pre Update
    // =========================

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    // =========================
    // Helper Method
    // =========================

    public void addItem(PrescriptionItem item) {

        items.add(item);
        item.setPrescription(this);
    }

    public void removeItem(PrescriptionItem item) {

        items.remove(item);
        item.setPrescription(null);
    }

    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}