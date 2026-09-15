package doctor.backend.controller;

import doctor.backend.dto.sms.SmsRequest;
import doctor.backend.dto.sms.SmsResponse;
import doctor.backend.service.SmsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "*")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    // =====================================================
    // SEND SMS
    // POST /api/sms
    // =====================================================

    @PostMapping
    public ResponseEntity<SmsResponse> sendSms(
            @RequestBody SmsRequest request) {

        SmsResponse response =
                smsService.sendSms(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL SMS LOGS
    // GET /api/sms
    // =====================================================

    @GetMapping
    public ResponseEntity<List<SmsResponse>> getAllSmsLogs() {

        return ResponseEntity.ok(
                smsService.getAllSmsLogs()
        );
    }

    // =====================================================
    // GET SMS BY ID
    // GET /api/sms/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<SmsResponse> getSmsById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                smsService.getSmsById(id)
        );
    }

    // =====================================================
    // GET BY PHONE NUMBER
    // GET /api/sms/phone/{phoneNumber}
    // =====================================================

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<List<SmsResponse>>
    getByPhoneNumber(
            @PathVariable String phoneNumber) {

        return ResponseEntity.ok(
                smsService.getByPhoneNumber(phoneNumber)
        );
    }

    // =====================================================
    // GET BY STATUS
    // GET /api/sms/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SmsResponse>>
    getByStatus(@PathVariable String status) {

        return ResponseEntity.ok(
                smsService.getByStatus(status)
        );
    }

    // =====================================================
    // GET BY TYPE
    // GET /api/sms/type/{type}
    // =====================================================

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SmsResponse>>
    getByType(@PathVariable String type) {

        return ResponseEntity.ok(
                smsService.getByType(type)
        );
    }

    // =====================================================
    // GET BY PROVIDER
    // GET /api/sms/provider/{provider}
    // =====================================================

    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<SmsResponse>>
    getByProvider(@PathVariable String provider) {

        return ResponseEntity.ok(
                smsService.getByProvider(provider)
        );
    }

    // =====================================================
    // GET BY PHONE + STATUS
    // GET /api/sms/search
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<List<SmsResponse>>
    getByPhoneAndStatus(

            @RequestParam String phoneNumber,

            @RequestParam String status) {

        return ResponseEntity.ok(
                smsService.getByPhoneAndStatus(
                        phoneNumber,
                        status
                )
        );
    }

    // =====================================================
    // GET BY PROVIDER MESSAGE ID
    // GET /api/sms/provider-message/{providerMessageId}
    // =====================================================

    @GetMapping("/provider-message/{providerMessageId}")
    public ResponseEntity<List<SmsResponse>>
    getByProviderMessageId(
            @PathVariable String providerMessageId) {

        return ResponseEntity.ok(
                smsService.getByProviderMessageId(
                        providerMessageId
                )
        );
    }

    // =====================================================
    // UPDATE SMS STATUS
    // PUT /api/sms/{id}/status
    // =====================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<SmsResponse> updateStatus(

            @PathVariable Long id,

            @RequestParam String status,

            @RequestParam(required = false)
            String providerMessageId,

            @RequestParam(required = false)
            String errorMessage) {

        return ResponseEntity.ok(
                smsService.updateStatus(
                        id,
                        status,
                        providerMessageId,
                        errorMessage
                )
        );
    }

    // =====================================================
    // DELETE SMS LOG
    // DELETE /api/sms/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSmsLog(
            @PathVariable Long id) {

        smsService.deleteSmsLog(id);

        return ResponseEntity.noContent().build();
    }
}