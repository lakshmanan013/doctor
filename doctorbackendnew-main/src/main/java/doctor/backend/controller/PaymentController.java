package doctor.backend.controller;

import doctor.backend.dto.payment.PaymentRequest;
import doctor.backend.dto.payment.PaymentResponse;
import doctor.backend.service.PaymentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // =====================================================
    // CREATE PAYMENT
    // POST /api/payments
    // =====================================================

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.createPayment(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL PAYMENTS
    // GET /api/payments
    // =====================================================

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    // =====================================================
    // GET PAYMENT BY ID
    // GET /api/payments/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }

    // =====================================================
    // GET PAYMENTS BY INVOICE
    // GET /api/payments/invoice/{invoiceId}
    // =====================================================

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponse>> getByInvoiceId(
            @PathVariable Long invoiceId) {

        return ResponseEntity.ok(
                paymentService.getByInvoiceId(invoiceId)
        );
    }

    // =====================================================
    // GET PAYMENTS BY OWNER
    // GET /api/payments/owner/{ownerId}
    // =====================================================

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PaymentResponse>> getByOwnerId(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                paymentService.getByOwnerId(ownerId)
        );
    }

    // =====================================================
    // GET PAYMENTS BY PATIENT
    // GET /api/payments/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaymentResponse>> getByPatientId(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                paymentService.getByPatientId(patientId)
        );
    }

    // =====================================================
    // GET PAYMENTS BY STATUS
    // GET /api/payments/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                paymentService.getByStatus(status)
        );
    }

    // =====================================================
    // GET PAYMENTS BY METHOD
    // GET /api/payments/method/{paymentMethod}
    // =====================================================

    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<PaymentResponse>> getByPaymentMethod(
            @PathVariable String paymentMethod) {

        return ResponseEntity.ok(
                paymentService.getByPaymentMethod(
                        paymentMethod
                )
        );
    }

    // =====================================================
    // GET BY TRANSACTION ID
    // GET /api/payments/transaction/{transactionId}
    // =====================================================

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<PaymentResponse>> getByTransactionId(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                paymentService.getByTransactionId(
                        transactionId
                )
        );
    }

    // =====================================================
    // GET BY REFERENCE NUMBER
    // GET /api/payments/reference/{referenceNumber}
    // =====================================================

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<List<PaymentResponse>> getByReferenceNumber(
            @PathVariable String referenceNumber) {

        return ResponseEntity.ok(
                paymentService.getByReferenceNumber(
                        referenceNumber
                )
        );
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // PUT /api/payments/{id}/status
    // =====================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updateStatus(
                        id,
                        status
                )
        );
    }

    // =====================================================
    // DELETE PAYMENT
    // DELETE /api/payments/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }
}