package doctor.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Sent by the Zenve admin backend to POST /api/internal/doctors/status
 * whenever an admin approves or rejects a doctor's registration.
 */
public class DoctorStatusUpdateRequest {

    @NotBlank(message = "Email is required")
    private String email;

    /** APPROVED or REJECTED */
    @NotBlank(message = "Status is required")
    private String status;

    private String reason;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
