package doctor.backend.service;

import doctor.backend.dto.auth.AuthResponse;
import doctor.backend.dto.auth.ForgotPasswordResponse;
import doctor.backend.dto.auth.LoginRequest;
import doctor.backend.dto.auth.MessageResponse;
import doctor.backend.dto.auth.RegisterRequest;
import doctor.backend.dto.auth.VerifyOtpResponse;
import doctor.backend.entity.User;
import doctor.backend.exception.BadRequestException;
import doctor.backend.exception.ForbiddenException;
import doctor.backend.repository.UserRepository;
import doctor.backend.security.CustomUserDetailsService;
import doctor.backend.security.JwtService;
import doctor.backend.util.OtpUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final AdminApprovalClient adminApprovalClient;
    private final PasswordResetMailer passwordResetMailer;
    private final ZippyCrmSyncService zippyCrmSyncService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            AdminApprovalClient adminApprovalClient,
            PasswordResetMailer passwordResetMailer,
            ZippyCrmSyncService zippyCrmSyncService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.adminApprovalClient = adminApprovalClient;
        this.passwordResetMailer = passwordResetMailer;
        this.zippyCrmSyncService = zippyCrmSyncService;
    }

    // =====================================================
    // REGISTER (Doctor create account)
    // =====================================================

    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists");
        }

        String phone = (request.getPhone() == null || request.getPhone().isBlank())
                ? null
                : request.getPhone().trim();

        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BadRequestException("An account with this phone number already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("DOCTOR");
        user.setActive(true);

        // Doctor registrations start PENDING. An admin must approve the
        // registration (via the Zenve admin portal) before the doctor
        // can log in.
        user.setApprovalStatus("PENDING");

        User saved = userRepository.save(user);

        // Let the admin backend know a new doctor is waiting for approval.
        // The account cannot log in until an admin approves it there.
        adminApprovalClient.notifyDoctorRegistered(saved);

        // Sync pending doctor to Zippy CRM
        zippyCrmSyncService.syncDoctor(saved);

        // No token on purpose: registering does not log the doctor in.
        return new AuthResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getRole(),
                null,
                saved.getApprovalStatus(),
                "Your account has been created and is pending admin approval. " +
                        "You'll be able to log in once an admin approves your registration.");
    }

    // =====================================================
    // ADMIN-CREATED ACCOUNT
    // Called by the Zenve admin backend when an admin creates a doctor
    // account directly. The account is APPROVED immediately (no pending
    // approval step) since an admin is vouching for it.
    // =====================================================

    public void createApprovedAccount(String fullName, String email, String phone, String rawPassword) {

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with this email already exists");
        }

        String normalizedPhone = (phone == null || phone.isBlank()) ? null : phone.trim();

        if (normalizedPhone != null && userRepository.existsByPhone(normalizedPhone)) {
            throw new BadRequestException("An account with this phone number already exists");
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("DOCTOR");
        user.setActive(true);
        user.setApprovalStatus("APPROVED");

        User saved = userRepository.save(user);
        zippyCrmSyncService.syncDoctor(saved);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if ("DOCTOR".equals(user.getRole())) {
            String status = user.getApprovalStatus();

            if ("PENDING".equals(status) || status == null) {
                throw new ForbiddenException(
                        "Your account is pending admin approval. Please try again once an admin has approved it.");
            }

            if ("REJECTED".equals(status)) {
                String reason = user.getRejectionReason();
                throw new ForbiddenException(
                        "Your registration was rejected" + (reason != null && !reason.isBlank() ? ": " + reason : "."));
            }
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("doctorId", user.getId());
        claims.put("userId", user.getId());
        String token = jwtService.generateToken(claims, userDetails);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                token);
    }

    // =====================================================
    // ADMIN CALLBACK — update approval status
    // Called by the Zenve admin backend when an admin approves/rejects
    // this doctor's registration.
    // =====================================================

    public void updateApprovalStatus(String email, String status, String reason) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("No account found for this email"));

        user.setApprovalStatus(status);
        user.setRejectionReason("REJECTED".equals(status) ? reason : null);
        User saved = userRepository.save(user);
        zippyCrmSyncService.syncDoctor(saved);
    }

    // =====================================================
    // FORGOT PASSWORD — step 1: email a 6-digit OTP
    //
    // Returns a "session" resetToken that just ties the following
    // verify-otp call to this request; on its own it can't be used to
    // change the password (see resetPassword below).
    // =====================================================

    public ForgotPasswordResponse forgotPassword(String email) {
        String normalized = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new BadRequestException("No account found with this email address"));

        String otp = OtpUtil.generateOtp();
        String sessionToken = UUID.randomUUID().toString();

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setOtpVerified(false);
        user.setResetToken(sessionToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        passwordResetMailer.sendOtp(user.getEmail(), otp);

        return new ForgotPasswordResponse("An OTP has been sent to your email address.", sessionToken);
    }

    // =====================================================
    // FORGOT PASSWORD — step 2: verify the OTP
    //
    // On success, issues a NEW resetToken that resetPassword() will
    // accept. The OTP is cleared so it can't be reused.
    // =====================================================

    public VerifyOtpResponse verifyOtp(String email, String otp, String resetToken) {
        String normalized = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new BadRequestException("Invalid request"));

        if (user.getResetToken() == null
                || !user.getResetToken().equals(resetToken)
                || user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This reset session has expired. Please request a new OTP");
        }

        if (user.getOtp() == null
                || !user.getOtp().equals(otp)
                || user.getOtpExpiry() == null
                || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        String verifiedToken = UUID.randomUUID().toString();

        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpVerified(true);
        user.setResetToken(verifiedToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        return new VerifyOtpResponse(true, "OTP verified successfully.", verifiedToken);
    }

    // =====================================================
    // FORGOT PASSWORD — step 3: set the new password
    //
    // Only accepts the resetToken issued by a successful verifyOtp()
    // call, so an attacker can't skip straight from step 1 to here.
    // =====================================================

    public MessageResponse resetPassword(String email, String resetToken, String newPassword) {
        String normalized = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new BadRequestException("Invalid request"));

        if (!user.isOtpVerified()
                || user.getResetToken() == null
                || !user.getResetToken().equals(resetToken)
                || user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This reset session has expired. Please start the reset process again");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpVerified(false);
        userRepository.save(user);

        return new MessageResponse("Password has been reset successfully. You can now log in with your new password.");
    }

    // =====================================================
    // CURRENT USER (from token)
    // =====================================================

    public AuthResponse getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Account not found"));

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                null);
    }
}
