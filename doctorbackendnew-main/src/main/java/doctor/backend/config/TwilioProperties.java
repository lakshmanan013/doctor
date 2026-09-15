package doctor.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {

    private String accountSid;
    private String authToken;
    private String smsFrom;
    private String whatsappFrom;

    // Only needed when accountSid above is an API Key SID (starts with
    // "SK") rather than a classic Account SID (starts with "AC"). Twilio's
    // API keys authenticate a request but don't identify which account it
    // belongs to, so the Java SDK needs the real Account SID (found on the
    // Twilio Console dashboard, starts with "AC") passed separately.
    private String parentAccountSid;

    public boolean isApiKey() {
        return accountSid != null && accountSid.startsWith("SK");
    }

    public boolean isConfigured() {
        boolean hasBasics = accountSid != null && !accountSid.isBlank()
                && authToken != null && !authToken.isBlank();
        if (!hasBasics) {
            return false;
        }
        // An API Key SID alone isn't enough to authenticate - Twilio also
        // needs to know which account the key belongs to.
        if (isApiKey()) {
            return parentAccountSid != null && !parentAccountSid.isBlank();
        }
        return true;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getSmsFrom() {
        return smsFrom;
    }

    public void setSmsFrom(String smsFrom) {
        this.smsFrom = smsFrom;
    }

    public String getWhatsappFrom() {
        return whatsappFrom;
    }

    public void setWhatsappFrom(String whatsappFrom) {
        this.whatsappFrom = whatsappFrom;
    }

    public String getParentAccountSid() {
        return parentAccountSid;
    }

    public void setParentAccountSid(String parentAccountSid) {
        this.parentAccountSid = parentAccountSid;
    }
}
