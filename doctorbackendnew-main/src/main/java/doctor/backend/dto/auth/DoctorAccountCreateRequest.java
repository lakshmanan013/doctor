package doctor.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Sent by the Zenve admin backend to POST /api/internal/doctors/create
 * whenever an admin creates a doctor account directly. Unlike self-registration
 * (see RegisterRequest), the resulting account is APPROVED immediately since
 * an admin is vouching for it.
 */
public class DoctorAccountCreateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
