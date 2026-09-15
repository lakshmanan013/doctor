package doctor.backend.dto.auth;

public class ForgotPasswordResponse {

    private String message;

    // Identifies this OTP session. On its own it does NOT authorize a
    // password change - verifyOtp() must also succeed before
    // resetPassword() will accept it (see AuthService).
    private String resetToken;

    public ForgotPasswordResponse() {
    }

    public ForgotPasswordResponse(String message, String resetToken) {
        this.message = message;
        this.resetToken = resetToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
