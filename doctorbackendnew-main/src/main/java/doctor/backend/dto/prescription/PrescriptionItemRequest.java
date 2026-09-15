package doctor.backend.dto.prescription;

public class PrescriptionItemRequest {

    private Long medicineId;

    private String dosage;

    private String frequency;

    private String duration;

    private String route;

    private String quantity;

    private String instructions;

    // =========================
    // Medicine
    // =========================

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
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
    // Frequency
    // =========================

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    // =========================
    // Duration
    // =========================

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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
    // Quantity
    // =========================

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
}
