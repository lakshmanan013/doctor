package doctor.backend.security;

import doctor.backend.entity.User;
import doctor.backend.exception.ForbiddenException;
import doctor.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Single place that resolves "which doctor is making this request".
 *
 * JwtAuthenticationFilter puts the doctor's email into the SecurityContext
 * (as the Authentication#getName()) once the JWT is validated. Every
 * controller that returns or mutates doctor-owned data (patients,
 * appointments, prescriptions, invoices, the doctor's own profile, etc.)
 * MUST go through this instead of trusting a doctorId/ownerId supplied by
 * the client - the client can never be trusted to say which doctor it is.
 */
@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the authenticated doctor's User row.
     *
     * Throws ForbiddenException (not a 500) if called on a request that
     * somehow reached a protected controller without a resolvable
     * authenticated user - that should never happen once SecurityConfig
     * requires authentication on these routes, but a controller must never
     * silently fall back to "no doctor" / "all doctors" here.
     */
    public User getCurrentDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new ForbiddenException("No authenticated doctor for this request");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException("No authenticated doctor for this request"));
    }

    public Long getCurrentDoctorId() {
        return getCurrentDoctor().getId();
    }

    public User getCurrentUser() {
        return getCurrentDoctor();
    }
}
