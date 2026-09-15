package doctor.backend.dto.email;

public class EmailRequest {

    private String recipient;

    private String subject;

    private String message;

    private String type;

    private String provider;

    // =========================
    // Recipient
    // =========================

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    // =========================
    // Subject
    // =========================

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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