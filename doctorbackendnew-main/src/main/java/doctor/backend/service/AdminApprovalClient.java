package doctor.backend.service;

import doctor.backend.config.AdminIntegrationProperties;
import doctor.backend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Talks to the Zenve admin backend so a new doctor signup shows up in the
 * admin's "pending approval" queue and triggers a notification there.
 *
 * Calls POST {admin base url}/doctors/register, which is documented on that
 * side as "the public endpoint used by the doctor-facing app".
 */
@Service
public class AdminApprovalClient {

    private static final Logger log = LoggerFactory.getLogger(AdminApprovalClient.class);

    private final RestTemplate restTemplate;
    private final AdminIntegrationProperties properties;

    public AdminApprovalClient(RestTemplate restTemplate, AdminIntegrationProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Best-effort: a doctor account is still created locally even if the admin
     * backend is unreachable, so a temporary outage there never blocks signup.
     * The account simply stays PENDING (and cannot log in) until an admin
     * approves it, which can happen once connectivity is restored and the
     * doctor is re-submitted, or via the admin's own "register" tooling.
     */
    public void notifyDoctorRegistered(User user) {
        String url = properties.getBaseUrl() + "/doctors/register";

        Map<String, String> body = Map.of(
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "phone", user.getPhone() == null ? "" : user.getPhone()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Void.class);
        } catch (RestClientException ex) {
            log.warn("Could not notify admin backend about new doctor registration ({}): {}",
                    user.getEmail(), ex.getMessage());
        }
    }
}
