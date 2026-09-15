package doctor.backend.controller;

import doctor.backend.dto.auth.AuthResponse;
import doctor.backend.dto.auth.ForgotPasswordRequest;
import doctor.backend.dto.auth.ForgotPasswordResponse;
import doctor.backend.dto.auth.LoginRequest;
import doctor.backend.dto.auth.MessageResponse;
import doctor.backend.dto.auth.RegisterRequest;
import doctor.backend.dto.auth.ResetPasswordRequest;
import doctor.backend.dto.auth.VerifyOtpRequest;
import doctor.backend.dto.auth.VerifyOtpResponse;
import doctor.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =====================================================
    // DOCTOR CREATE ACCOUNT
    // POST /api/auth/register
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =====================================================
    // DOCTOR LOGIN
    // POST /api/auth/login
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // =====================================================
    // CURRENT LOGGED-IN DOCTOR
    // GET /api/auth/me
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
    }

    // =====================================================
    // FORGOT PASSWORD — step 1: email a 6-digit OTP
    // POST /api/auth/forgot-password
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    // =====================================================
    // FORGOT PASSWORD — step 2: verify the OTP
    // POST /api/auth/verify-otp
    // =====================================================

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

        return ResponseEntity.ok(
                authService.verifyOtp(request.getEmail(), request.getOtp(), request.getResetToken()));
    }

    // =====================================================
    // FORGOT PASSWORD — step 3: set the new password
    // POST /api/auth/reset-password
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(
                authService.resetPassword(request.getEmail(), request.getResetToken(), request.getNewPassword()));
    }
}
