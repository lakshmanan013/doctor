package doctor.backend.dto.auth;

public class VerifyOtpResponse {

    private boolean verified;
    private String message;

    // A freshly-issued token proving the OTP step passed. Only this
    // token (not the one from forgot-password) will be accepted by
    // resetPassword().
    private String resetToken;

    public VerifyOtpResponse() {
    }

    public VerifyOtpResponse(boolean verified, String message, String resetToken) {
        this.verified = verified;
        this.message = message;
        this.resetToken = resetToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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
