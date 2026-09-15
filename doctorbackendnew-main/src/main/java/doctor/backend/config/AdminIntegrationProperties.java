package doctor.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminIntegrationProperties {

    /** Base URL of the Zenve admin backend, e.g. http://localhost:5000/api */
    private String baseUrl;

    /** Shared secret the admin backend must present when pushing status updates back. */
    private String internalSecret;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInternalSecret() {
        return internalSecret;
    }

    public void setInternalSecret(String internalSecret) {
        this.internalSecret = internalSecret;
    }
}
