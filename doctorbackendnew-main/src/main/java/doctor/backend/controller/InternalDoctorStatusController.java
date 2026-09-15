package doctor.backend.controller;

import doctor.backend.config.AdminIntegrationProperties;
import doctor.backend.dto.auth.DoctorAccountCreateRequest;
import doctor.backend.dto.auth.DoctorStatusUpdateRequest;
import doctor.backend.dto.auth.DoctorVerificationUpdateRequest;
import doctor.backend.entity.User;
import doctor.backend.exception.ForbiddenException;
import doctor.backend.exception.ResourceNotFoundException;
import doctor.backend.repository.UserRepository;
import doctor.backend.service.AuthService;
import doctor.backend.service.DoctorProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Called by the Zenve admin backend whenever an admin approves or rejects a
 * doctor's registration, verifies a profile item, or creates a doctor
 * account directly, so this app can allow (or keep blocking) login and
 * reflect verification status.
 *
 * Not meant to be called from a browser: it's protected by a shared secret
 * header rather than the normal doctor JWT auth, and is excluded from the
 * public CORS/security rules in SecurityConfig.
 */
@RestController
@RequestMapping("/api/internal/doctors")
public class InternalDoctorStatusController {

    private static final String SECRET_HEADER = "X-Internal-Secret";

    private final AuthService authService;
    private final DoctorProfileService doctorProfileService;
    private final UserRepository userRepository;
    private final AdminIntegrationProperties properties;

    public InternalDoctorStatusController(AuthService authService, DoctorProfileService doctorProfileService,
                                           UserRepository userRepository, AdminIntegrationProperties properties) {
        this.authService = authService;
        this.doctorProfileService = doctorProfileService;
        this.userRepository = userRepository;
        this.properties = properties;
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateStatus(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret,
            @Valid @RequestBody DoctorStatusUpdateRequest request
    ) {
        checkSecret(secret);

        authService.updateApprovalStatus(request.getEmail(), request.getStatus(), request.getReason());

        return ResponseEntity.ok().build();
    }

    /**
     * Called when an admin clicks "verify" on one of the Verification panel
     * items (Veterinary registration, KYC verification, Digital signature,
     * State council sync).
     */
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret,
            @Valid @RequestBody DoctorVerificationUpdateRequest request
    ) {
        checkSecret(secret);

        User doctor = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No doctor account found for email: " + request.getEmail()));

        doctorProfileService.applyVerification(doctor.getId(), request.getItem());

        return ResponseEntity.ok().build();
    }

    /**
     * Called when an admin creates a doctor account directly from the admin
     * CRM. The resulting account can log in immediately (APPROVED).
     */
    @PostMapping("/create")
    public ResponseEntity<Void> create(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret,
            @Valid @RequestBody DoctorAccountCreateRequest request
    ) {
        checkSecret(secret);

        authService.createApprovedAccount(
                request.getFullName(), request.getEmail(), request.getPhone(), request.getPassword());

        return ResponseEntity.ok().build();
    }

    private void checkSecret(String secret) {
        if (properties.getInternalSecret() == null || !properties.getInternalSecret().equals(secret)) {
            throw new ForbiddenException("Invalid internal secret");
        }
    }
}
