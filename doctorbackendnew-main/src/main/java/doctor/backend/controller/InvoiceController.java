package doctor.backend.controller;

import doctor.backend.dto.invoice.InvoiceRequest;
import doctor.backend.dto.invoice.InvoiceResponse;
import doctor.backend.service.InvoiceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // =====================================================
    // CREATE INVOICE
    // POST /api/invoices
    // =====================================================

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestBody InvoiceRequest request) {

        InvoiceResponse response =
                invoiceService.createInvoice(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL INVOICES
    // GET /api/invoices
    // =====================================================

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {

        return ResponseEntity.ok(
                invoiceService.getAllInvoices()
        );
    }

    // =====================================================
    // GET INVOICE BY ID
    // GET /api/invoices/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(id)
        );
    }

    // =====================================================
    // GET BY INVOICE NUMBER
    // GET /api/invoices/number/{invoiceNumber}
    // =====================================================

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getByInvoiceNumber(
            @PathVariable String invoiceNumber) {

        return ResponseEntity.ok(
                invoiceService.getByInvoiceNumber(
                        invoiceNumber
                )
        );
    }

    // =====================================================
    // GET BY OWNER
    // GET /api/invoices/owner/{ownerId}
    // =====================================================

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<InvoiceResponse>> getByOwnerId(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                invoiceService.getByOwnerId(ownerId)
        );
    }

    // =====================================================
    // GET BY PATIENT
    // GET /api/invoices/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<InvoiceResponse>> getByPatientId(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                invoiceService.getByPatientId(patientId)
        );
    }

    // =====================================================
    // GET BY STATUS
    // GET /api/invoices/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                invoiceService.getByStatus(status)
        );
    }

    // =====================================================
    // GET BY PAYMENT STATUS
    // GET /api/invoices/payment-status/{paymentStatus}
    // =====================================================

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<InvoiceResponse>>
    getByPaymentStatus(
            @PathVariable String paymentStatus) {

        return ResponseEntity.ok(
                invoiceService.getByPaymentStatus(
                        paymentStatus
                )
        );
    }

    // =====================================================
    // GET OWNER + PAYMENT STATUS
    // GET /api/invoices/owner/{ownerId}/payment-status/{paymentStatus}
    // =====================================================

    @GetMapping(
            "/owner/{ownerId}/payment-status/{paymentStatus}"
    )
    public ResponseEntity<List<InvoiceResponse>>
    getByOwnerAndPaymentStatus(
            @PathVariable Long ownerId,
            @PathVariable String paymentStatus) {

        return ResponseEntity.ok(
                invoiceService.getByOwnerAndPaymentStatus(
                        ownerId,
                        paymentStatus
                )
        );
    }

    // =====================================================
    // GET PATIENT + PAYMENT STATUS
    // GET /api/invoices/patient/{patientId}/payment-status/{paymentStatus}
    // =====================================================

    @GetMapping(
            "/patient/{patientId}/payment-status/{paymentStatus}"
    )
    public ResponseEntity<List<InvoiceResponse>>
    getByPatientAndPaymentStatus(
            @PathVariable Long patientId,
            @PathVariable String paymentStatus) {

        return ResponseEntity.ok(
                invoiceService.getByPatientAndPaymentStatus(
                        patientId,
                        paymentStatus
                )
        );
    }

    // =====================================================
    // UPDATE PAYMENT
    // PUT /api/invoices/{id}/payment
    // =====================================================

    @PutMapping("/{id}/payment")
    public ResponseEntity<InvoiceResponse> updatePayment(
            @PathVariable Long id,
            @RequestParam BigDecimal paidAmount) {

        return ResponseEntity.ok(
                invoiceService.updatePayment(
                        id,
                        paidAmount
                )
        );
    }

    // =====================================================
    // CANCEL INVOICE
    // PUT /api/invoices/{id}/cancel
    // =====================================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancelInvoice(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                invoiceService.cancelInvoice(id)
        );
    }

    // =====================================================
    // DELETE INVOICE
    // DELETE /api/invoices/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable Long id) {

        invoiceService.deleteInvoice(id);

        return ResponseEntity.noContent().build();
    }
}