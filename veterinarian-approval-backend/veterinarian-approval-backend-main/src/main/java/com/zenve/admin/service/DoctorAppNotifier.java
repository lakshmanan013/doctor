package com.zenve.admin.service;

import com.zenve.admin.config.DoctorAppProperties;
import com.zenve.admin.model.Doctor;
import com.zenve.admin.model.VerificationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Pushes approve/reject decisions to the doctor-facing backend (vd-backend)
 * so it can allow or keep blocking that doctor's login. This is what actually
 * "unlocks" the doctor's account after an admin approves it here.
 */
@Service
public class DoctorAppNotifier {

    private static final Logger log = LoggerFactory.getLogger(DoctorAppNotifier.class);
    private static final String SECRET_HEADER = "X-Internal-Secret";

    private final RestTemplate restTemplate;
    private final DoctorAppProperties properties;

    public DoctorAppNotifier(RestTemplate restTemplate, DoctorAppProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void notifyApproved(Doctor doctor) {
        push(doctor.getEmail(), "APPROVED", null);
    }

    public void notifyRejected(Doctor doctor) {
        push(doctor.getEmail(), "REJECTED", doctor.getRejectionReason());
    }

    /**
     * Pushes a verified item to the doctor-facing backend so its Verification
     * panel reflects the admin's decision.
     */
    public void notifyVerified(Doctor doctor, VerificationItem item) {
        String url = properties.baseUrl() + "/internal/doctors/verify";

        Map<String, String> body = new HashMap<>();
        body.put("email", doctor.getEmail());
        body.put("item", item.name());

        post(url, body, "verification (" + item + ") for " + doctor.getEmail());
    }

    /**
     * Creates the doctor's login account on the doctor-facing backend when an
     * admin creates the doctor directly from the admin CRM.
     */
    public void notifyAccountCreated(Doctor doctor, String password) {
        String url = properties.baseUrl() + "/internal/doctors/create";

        Map<String, String> body = new HashMap<>();
        body.put("fullName", doctor.getFullName());
        body.put("email", doctor.getEmail());
        body.put("phone", doctor.getPhone());
        body.put("password", password);

        post(url, body, "account creation for " + doctor.getEmail());
    }

    private void push(String email, String status, String reason) {
        String url = properties.baseUrl() + "/internal/doctors/status";

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("status", status);
        body.put("reason", reason);

        post(url, body, "status change for " + email);
    }

    private void post(String url, Map<String, String> body, String description) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(SECRET_HEADER, properties.internalSecret());

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Void.class);
        } catch (RestClientException ex) {
            // The decision is still recorded here either way — this is just
            // the notification that syncs the other app, so a temporary
            // outage there shouldn't fail the admin's action.
            log.warn("Could not notify doctor-facing backend about {}: {}", description, ex.getMessage());
        }
    }
}
