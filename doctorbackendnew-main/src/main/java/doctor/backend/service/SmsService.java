package doctor.backend.service;

import doctor.backend.dto.sms.SmsRequest;
import doctor.backend.dto.sms.SmsResponse;
import doctor.backend.entity.SmsLog;
import doctor.backend.repository.SmsLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SmsService {

    private final SmsLogRepository smsLogRepository;
    private final TwilioSender twilioSender;

    public SmsService(SmsLogRepository smsLogRepository, TwilioSender twilioSender) {
        this.smsLogRepository = smsLogRepository;
        this.twilioSender = twilioSender;
    }

    // =====================================================
    // SEND / CREATE SMS LOG
    // =====================================================

    public SmsResponse sendSms(SmsRequest request) {

        if (request.getPhoneNumber() == null ||
                request.getPhoneNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone number is required"
            );
        }

        if (request.getMessage() == null ||
                request.getMessage().trim().isEmpty()) {

            throw new RuntimeException(
                    "SMS message is required"
            );
        }

        SmsLog smsLog = new SmsLog();

        smsLog.setPhoneNumber(
                request.getPhoneNumber()
        );

        smsLog.setMessage(
                request.getMessage()
        );

        smsLog.setType(
                request.getType() != null &&
                        !request.getType().trim().isEmpty()
                        ? request.getType()
                        : "General"
        );

        smsLog.setProvider(
                request.getProvider() != null &&
                        !request.getProvider().trim().isEmpty()
                        ? request.getProvider()
                        : "Default"
        );

        /*
         * Attempt a real send via Twilio. If no Twilio account is configured
         * yet, record that honestly instead of a silent no-op "Pending".
         */
        if (!twilioSender.isConfigured()) {
            smsLog.setStatus("NOT_CONFIGURED");
            smsLog.setErrorMessage("SMS provider isn't configured on the server yet (no Twilio account set up)");
        } else {
            try {
                String messageSid = twilioSender.sendSms(smsLog.getPhoneNumber(), smsLog.getMessage());
                smsLog.setStatus("SENT");
                smsLog.setProviderMessageId(messageSid);
            } catch (RuntimeException ex) {
                smsLog.setStatus("FAILED");
                smsLog.setErrorMessage(ex.getMessage());
            }
        }

        SmsLog saved =
                smsLogRepository.save(smsLog);

        return mapToResponse(saved);
    }

    // =====================================================
    // GET ALL SMS LOGS
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getAllSmsLogs() {

        return smsLogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET SMS BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public SmsResponse getSmsById(Long id) {

        SmsLog smsLog =
                smsLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SMS log not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(smsLog);
    }

    // =====================================================
    // GET BY PHONE NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getByPhoneNumber(
            String phoneNumber) {

        return smsLogRepository
                .findByPhoneNumber(phoneNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getByStatus(
            String status) {

        return smsLogRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY TYPE
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getByType(
            String type) {

        return smsLogRepository
                .findByType(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PROVIDER
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getByProvider(
            String provider) {

        return smsLogRepository
                .findByProvider(provider)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PHONE + STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<SmsResponse> getByPhoneAndStatus(
            String phoneNumber,
            String status) {

        return smsLogRepository
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
    public List<SmsResponse> getByProviderMessageId(
            String providerMessageId) {

        return smsLogRepository
                .findByProviderMessageId(
                        providerMessageId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE SMS STATUS
    // =====================================================

    public SmsResponse updateStatus(
            Long id,
            String status,
            String providerMessageId,
            String errorMessage) {

        SmsLog smsLog =
                smsLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SMS log not found with id: "
                                                + id
                                )
                        );

        if (status != null &&
                !status.trim().isEmpty()) {

            smsLog.setStatus(status);
        }

        if (providerMessageId != null) {

            smsLog.setProviderMessageId(
                    providerMessageId
            );
        }

        if (errorMessage != null) {

            smsLog.setErrorMessage(
                    errorMessage
            );
        }

        SmsLog updated =
                smsLogRepository.save(smsLog);

        return mapToResponse(updated);
    }

    // =====================================================
    // DELETE SMS LOG
    // =====================================================

    public void deleteSmsLog(Long id) {

        if (!smsLogRepository.existsById(id)) {

            throw new RuntimeException(
                    "SMS log not found with id: " + id
            );
        }

        smsLogRepository.deleteById(id);
    }

    // =====================================================
    // MAP ENTITY TO RESPONSE
    // =====================================================

    private SmsResponse mapToResponse(
            SmsLog smsLog) {

        SmsResponse response =
                new SmsResponse();

        response.setId(
                smsLog.getId()
        );

        response.setPhoneNumber(
                smsLog.getPhoneNumber()
        );

        response.setMessage(
                smsLog.getMessage()
        );

        response.setType(
                smsLog.getType()
        );

        response.setStatus(
                smsLog.getStatus()
        );

        response.setProvider(
                smsLog.getProvider()
        );

        response.setProviderMessageId(
                smsLog.getProviderMessageId()
        );

        response.setErrorMessage(
                smsLog.getErrorMessage()
        );

        response.setCreatedAt(
                smsLog.getCreatedAt()
        );

        response.setUpdatedAt(
                smsLog.getUpdatedAt()
        );

        return response;
    }
}