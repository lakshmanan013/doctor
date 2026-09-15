package doctor.backend.dto.whatsapp;

public class WhatsAppRequest {

    private String phoneNumber;

    private String message;

    private String type;

    private String provider;

    // =========================
    // Phone Number
    // =========================

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // =========================
    // Message
    // =========================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // =========================
    // Type
    // =========================

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // =========================
    // Provider
    // =========================

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}