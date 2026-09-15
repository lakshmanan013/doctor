package doctor.backend.service;

import doctor.backend.config.FrontendProperties;
import doctor.backend.config.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends the password-reset link by email. When spring.mail.host isn't
 * configured (local/dev), sending fails fast and the link is logged instead
 * so the reset flow is still testable without a real SMTP account.
 */
@Service
public class PasswordResetMailer {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetMailer.class);

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final FrontendProperties frontendProperties;

    public PasswordResetMailer(JavaMailSender mailSender, MailProperties mailProperties,
                                FrontendProperties frontendProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.frontendProperties = frontendProperties;
    }

    public void sendResetLink(String toEmail, String token) {
        String link = frontendProperties.getBaseUrl() + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(toEmail);
        message.setSubject("Reset your Zenve VetCare password");
        message.setText(
                "We received a request to reset your password.\n\n" +
                        "Reset it here (expires in 30 minutes): " + link + "\n\n" +
                        "If you didn't request this, you can ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            // Most likely spring.mail.host isn't configured. Don't fail the
            // request for it - log the link so the flow is still usable in dev.
            log.warn("Could not email password reset link to {}: {}. Reset link: {}",
                    toEmail, ex.getMessage(), link);
        }
    }

    // =====================================================
    // OTP-based forgot-password flow (see AuthService).
    // =====================================================

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(toEmail);
        message.setSubject("Your Zenve VetCare password reset code");
        message.setText(
                "We received a request to reset your password.\n\n" +
                        "Your one-time code is: " + otp + "\n\n" +
                        "This code expires in 10 minutes. If you didn't request this, you can ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            // Most likely spring.mail.host isn't configured. Don't fail the
            // request for it - log the OTP so the flow is still usable in dev.
            log.warn("Could not email password reset OTP to {}: {}. OTP: {}",
                    toEmail, ex.getMessage(), otp);
        }
    }
}
