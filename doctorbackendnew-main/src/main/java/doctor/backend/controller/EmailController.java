package doctor.backend.controller;

import doctor.backend.dto.email.EmailRequest;
import doctor.backend.dto.email.EmailResponse;
import doctor.backend.service.EmailService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // =====================================================
    // SEND EMAIL
    // POST /api/emails
    // =====================================================

    @PostMapping
    public ResponseEntity<EmailResponse> sendEmail(
            @RequestBody EmailRequest request) {

        EmailResponse response =
                emailService.sendEmail(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL EMAILS
    // GET /api/emails
    // =====================================================

    @GetMapping
    public ResponseEntity<List<EmailResponse>> getAllEmails() {

        return ResponseEntity.ok(
                emailService.getAllEmails()
        );
    }

    // =====================================================
    // GET EMAIL BY ID
    // GET /api/emails/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<EmailResponse> getEmailById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                emailService.getEmailById(id)
        );
    }

    // =====================================================
    // GET BY RECIPIENT
    // GET /api/emails/recipient/{recipient}
    // =====================================================

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<EmailResponse>>
    getByRecipient(
            @PathVariable String recipient) {

        return ResponseEntity.ok(
                emailService.getByRecipient(recipient)
        );
    }

    // =====================================================
    // GET BY STATUS
    // GET /api/emails/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailResponse>>
    getByStatus(@PathVariable String status) {

        return ResponseEntity.ok(
                emailService.getByStatus(status)
        );
    }

    // =====================================================
    // GET BY TYPE
    // GET /api/emails/type/{type}
    // =====================================================

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EmailResponse>>
    getByType(@PathVariable String type) {

        return ResponseEntity.ok(
                emailService.getByType(type)
        );
    }

    // =====================================================
    // GET BY PROVIDER
    // GET /api/emails/provider/{provider}
    // =====================================================

    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<EmailResponse>>
    getByProvider(@PathVariable String provider) {

        return ResponseEntity.ok(
                emailService.getByProvider(provider)
        );
    }

    // =====================================================
    // GET BY RECIPIENT + STATUS
    // GET /api/emails/search
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<List<EmailResponse>>
    getByRecipientAndStatus(
            @RequestParam String recipient,
            @RequestParam String status) {

        return ResponseEntity.ok(
                emailService.getByRecipientAndStatus(
                        recipient,
                        status
                )
        );
    }

    // =====================================================
    // GET BY PROVIDER MESSAGE ID
    // GET /api/emails/provider-message/{providerMessageId}
    // =====================================================

    @GetMapping("/provider-message/{providerMessageId}")
    public ResponseEntity<List<EmailResponse>>
    getByProviderMessageId(
            @PathVariable String providerMessageId) {

        return ResponseEntity.ok(
                emailService.getByProviderMessageId(
                        providerMessageId
                )
        );
    }

    // =====================================================
    // UPDATE EMAIL STATUS
    // PUT /api/emails/{id}/status
    // =====================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<EmailResponse>
    updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false)
            String providerMessageId,
            @RequestParam(required = false)
            String errorMessage) {

        return ResponseEntity.ok(
                emailService.updateStatus(
                        id,
                        status,
                        providerMessageId,
                        errorMessage
                )
        );
    }

    // =====================================================
    // DELETE EMAIL LOG
    // DELETE /api/emails/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(
            @PathVariable Long id) {

        emailService.deleteEmail(id);

        return ResponseEntity.noContent().build();
    }
}