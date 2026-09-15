package doctor.backend.service;

import doctor.backend.dto.whatsapp.WhatsAppRequest;
import doctor.backend.dto.whatsapp.WhatsAppResponse;
import doctor.backend.entity.WhatsAppLog;
import doctor.backend.repository.WhatsAppLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WhatsAppService {

    private final WhatsAppLogRepository whatsAppLogRepository;
    private final TwilioSender twilioSender;

    public WhatsAppService(
            WhatsAppLogRepository whatsAppLogRepository,
            TwilioSender twilioSender) {

        this.whatsAppLogRepository = whatsAppLogRepository;
        this.twilioSender = twilioSender;
    }

    // =====================================================
    // SEND / CREATE WHATSAPP LOG
    // =====================================================

    public WhatsAppResponse sendMessage(
            WhatsAppRequest request) {

        if (request.getPhoneNumber() == null ||
                request.getPhoneNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone number is required"
            );
        }

        if (request.getMessage() == null ||
                request.getMessage().trim().isEmpty()) {

            throw new RuntimeException(
                    "WhatsApp message is required"
            );
        }

        WhatsAppLog log = new WhatsAppLog();

        log.setPhoneNumber(
                request.getPhoneNumber()
        );

        log.setMessage(
                request.getMessage()
        );

        log.setType(
                request.getType() != null &&
                        !request.getType().trim().isEmpty()
                        ? request.getType()
                        : "General"
        );

        log.setProvider(
                request.getProvider() != null &&
                        !request.getProvider().trim().isEmpty()
                        ? request.getProvider()
                        : "Default"
        );

        /*
         * Attempt a real send via Twilio's WhatsApp API. If no Twilio
         * account is configured yet, record that honestly instead of a
         * silent no-op "Pending".
         */
        if (!twilioSender.isConfigured()) {
            log.setStatus("NOT_CONFIGURED");
            log.setErrorMessage("WhatsApp provider isn't configured on the server yet (no Twilio account set up)");
        } else {
            try {
                String messageSid = twilioSender.sendWhatsApp(log.getPhoneNumber(), log.getMessage());
                log.setStatus("SENT");
                log.setProviderMessageId(messageSid);
            } catch (RuntimeException ex) {
                log.setStatus("FAILED");
                log.setErrorMessage(ex.getMessage());
            }
        }

        WhatsAppLog saved =
                whatsAppLogRepository.save(log);

        return mapToResponse(saved);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getAllMessages() {

        return whatsAppLogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public WhatsAppResponse getMessageById(Long id) {

        WhatsAppLog log =
                whatsAppLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "WhatsApp log not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(log);
    }

    // =====================================================
    // GET BY PHONE
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getByPhoneNumber(
            String phoneNumber) {

        return whatsAppLogRepository
                .findByPhoneNumber(phoneNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getByStatus(
            String status) {

        return whatsAppLogRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY TYPE
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getByType(
            String type) {

        return whatsAppLogRepository
                .findByType(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PROVIDER
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getByProvider(
            String provider) {

        return whatsAppLogRepository
                .findByProvider(provider)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PHONE + STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<WhatsAppResponse> getByPhoneAndStatus(
            String phoneNumber,
            String status) {

        return whatsAppLogRepository
                .findByPhoneNumberAndStatus(
                        phoneNumber,
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
    public List<WhatsAppResponse> getByProviderMessageId(
            String providerMessageId) {

        return whatsAppLogRepository
                .findByProviderMessageId(
                        providerMessageId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE STATUS
    // =====================================================

    public WhatsAppResponse updateStatus(
            Long id,
            String status,
            String providerMessageId,
            String errorMessage) {

        WhatsAppLog log =
                whatsAppLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "WhatsApp log not found with id: "
                                                + id
                                )
                        );

        if (status != null &&
                !status.trim().isEmpty()) {

            log.setStatus(status);
        }

        if (providerMessageId != null) {

            log.setProviderMessageId(
                    providerMessageId
            );
        }

        if (errorMessage != null) {

            log.setErrorMessage(
                    errorMessage
            );
        }

        WhatsAppLog updated =
                whatsAppLogRepository.save(log);

        return mapToResponse(updated);
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteMessage(Long id) {

        if (!whatsAppLogRepository.existsById(id)) {

            throw new RuntimeException(
                    "WhatsApp log not found with id: " + id
            );
        }

        whatsAppLogRepository.deleteById(id);
    }

    // =====================================================
    // MAP ENTITY TO RESPONSE
    // =====================================================

    private WhatsAppResponse mapToResponse(
            WhatsAppLog log) {

        WhatsAppResponse response =
                new WhatsAppResponse();

        response.setId(
                log.getId()
        );

        response.setPhoneNumber(
                log.getPhoneNumber()
        );

        response.setMessage(
                log.getMessage()
        );

        response.setType(
                log.getType()
        );

        response.setStatus(
                log.getStatus()
        );

        response.setProvider(
                log.getProvider()
        );

        response.setProviderMessageId(
                log.getProviderMessageId()
        );

        response.setErrorMessage(
                log.getErrorMessage()
        );

        response.setCreatedAt(
                log.getCreatedAt()
        );

        response.setUpdatedAt(
                log.getUpdatedAt()
        );

        return response;
    }
}