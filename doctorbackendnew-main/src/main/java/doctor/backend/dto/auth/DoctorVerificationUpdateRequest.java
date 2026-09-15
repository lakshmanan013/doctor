package doctor.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Sent by the Zenve admin backend to POST /api/internal/doctor-profile/verify
 * whenever an admin clicks "verify" on one of the Verification panel items.
 */
public class DoctorVerificationUpdateRequest {

    /**
     * Which doctor this verification applies to. Previously missing
     * entirely, which is why every "verify" click landed on the doctor_profile
     * row with id=1 no matter which doctor the admin had open.
     */
    @NotBlank(message = "Email is required")
    private String email;

    /** One of: VETERINARY_REGISTRATION, KYC, DIGITAL_SIGNATURE, STATE_COUNCIL_SYNC */
    @NotBlank(message = "Item is required")
    private String item;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
