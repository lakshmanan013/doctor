package doctor.backend.service;

import doctor.backend.config.MailProperties;
import doctor.backend.dto.email.EmailRequest;
import doctor.backend.dto.email.EmailResponse;
import doctor.backend.entity.EmailLog;
import doctor.backend.repository.EmailLogRepository;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmailService {

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public EmailService(EmailLogRepository emailLogRepository, JavaMailSender mailSender,
                         MailProperties mailProperties) {
        this.emailLogRepository = emailLogRepository;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    // =====================================================
    // SEND / CREATE EMAIL LOG
    // =====================================================

    public EmailResponse sendEmail(EmailRequest request) {

        if (request.getRecipient() == null ||
                request.getRecipient().trim().isEmpty()) {

            throw new RuntimeException(
                    "Recipient email is required"
            );
        }

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email subject is required"
            );
        }

        if (request.getMessage() == null ||
                request.getMessage().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email message is required"
            );
        }

        EmailLog emailLog = new EmailLog();

        emailLog.setRecipient(
                request.getRecipient()
        );

        emailLog.setSubject(
                request.getSubject()
        );

        emailLog.setMessage(
                request.getMessage()
        );

        emailLog.setType(
                request.getType() != null &&
                        !request.getType().trim().isEmpty()
                        ? request.getType()
                        : "General"
        );

        emailLog.setProvider(
                request.getProvider() != null &&
                        !request.getProvider().trim().isEmpty()
                        ? request.getProvider()
                        : "Default"
        );

        /*
         * Attempt a real send via the configured SMTP account. If
         * spring.mail.host isn't set (dev/no provider configured yet), this
         * fails fast and we record that honestly instead of claiming success.
         */
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailProperties.getFrom());
            mailMessage.setTo(emailLog.getRecipient());
            mailMessage.setSubject(emailLog.getSubject());
            mailMessage.setText(emailLog.getMessage());

            mailSender.send(mailMessage);

            emailLog.setStatus("SENT");
        } catch (MailException ex) {
            emailLog.setStatus("FAILED");
            emailLog.setErrorMessage(
                    "Email provider isn't configured or the send failed: " + ex.getMostSpecificCause().getMessage()
            );
        }

        EmailLog saved =
                emailLogRepository.save(emailLog);

        return mapToResponse(saved);
    }

    // =====================================================
    // GET ALL EMAIL LOGS
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getAllEmails() {

        return emailLogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public EmailResponse getEmailById(Long id) {

        EmailLog emailLog =
                emailLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email log not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(emailLog);
    }

    // =====================================================
    // GET BY RECIPIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByRecipient(
            String recipient) {

        return emailLogRepository
                .findByRecipient(recipient)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByStatus(
            String status) {

        return emailLogRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY TYPE
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByType(
            String type) {

        return emailLogRepository
                .findByType(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PROVIDER
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByProvider(
            String provider) {

        return emailLogRepository
                .findByProvider(provider)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY RECIPIENT + STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByRecipientAndStatus(
            String recipient,
            String status) {

        return emailLogRepository
                .findByRecipientAndStatus(
                        recipient,
                        status
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PROVIDER MESSAGE ID
    // =====================================================

    @Transactional(readOnly = true)
    public List<EmailResponse> getByProviderMessageId(
            String providerMessageId) {

        return emailLogRepository
                .findByProviderMessageId(
                        providerMessageId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE EMAIL STATUS
    // =====================================================

    public EmailResponse updateStatus(
            Long id,
            String status,
            String providerMessageId,
            String errorMessage) {

        EmailLog emailLog =
                emailLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email log not found with id: "
                                                + id
                                )
                        );

        if (status != null &&
                !status.trim().isEmpty()) {

            emailLog.setStatus(status);
        }

        if (providerMessageId != null) {

            emailLog.setProviderMessageId(
                    providerMessageId
            );
        }

        if (errorMessage != null) {

            emailLog.setErrorMessage(
                    errorMessage
            );
        }

        EmailLog updated =
                emailLogRepository.save(emailLog);

        return mapToResponse(updated);
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteEmail(Long id) {

        if (!emailLogRepository.existsById(id)) {

            throw new RuntimeException(
                    "Email log not found with id: " + id
            );
        }

        emailLogRepository.deleteById(id);
    }

    // =====================================================
    // MAP ENTITY TO RESPONSE
    // =====================================================

    private EmailResponse mapToResponse(
            EmailLog emailLog) {

        EmailResponse response =
                new EmailResponse();

        response.setId(
                emailLog.getId()
        );

        response.setRecipient(
                emailLog.getRecipient()
        );

        response.setSubject(
                emailLog.getSubject()
        );

        response.setMessage(
                emailLog.getMessage()
        );

        response.setType(
                emailLog.getType()
        );

        response.setStatus(
                emailLog.getStatus()
        );

        response.setProvider(
                emailLog.getProvider()
        );

        response.setProviderMessageId(
                emailLog.getProviderMessageId()
        );

        response.setErrorMessage(
                emailLog.getErrorMessage()
        );

        response.setCreatedAt(
                emailLog.getCreatedAt()
        );

        response.setUpdatedAt(
                emailLog.getUpdatedAt()
        );

        return response;
    }
}