package doctor.backend.service;

import doctor.backend.dto.payment.PaymentRequest;
import doctor.backend.dto.payment.PaymentResponse;
import doctor.backend.entity.Invoice;
import doctor.backend.entity.Payment;
import doctor.backend.repository.InvoiceRepository;
import doctor.backend.repository.PaymentRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CurrentUserProvider currentUserProvider;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            CurrentUserProvider currentUserProvider) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE PAYMENT
    // =====================================================

    public PaymentResponse createPayment(
            PaymentRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (request.getInvoiceId() == null) {
            throw new RuntimeException(
                    "Invoice ID is required"
            );
        }

        if (request.getAmount() == null) {
            throw new RuntimeException(
                    "Payment amount is required"
            );
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Payment amount must be greater than zero"
            );
        }

        // Find invoice
        Invoice invoice =
                invoiceRepository.findByIdAndDoctorId(
                        request.getInvoiceId(),
                        doctorId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Invoice not found with id: "
                                        + request.getInvoiceId()
                        )
                );

        // Do not allow payment for cancelled invoice
        if ("CANCELLED".equalsIgnoreCase(
                invoice.getStatus())) {

            throw new RuntimeException(
                    "Cannot make payment for a cancelled invoice"
            );
        }

        BigDecimal currentPaid =
                invoice.getPaidAmount() != null
                        ? invoice.getPaidAmount()
                        : BigDecimal.ZERO;

        BigDecimal totalAmount =
                invoice.getTotalAmount() != null
                        ? invoice.getTotalAmount()
                        : BigDecimal.ZERO;

        BigDecimal remainingAmount =
                totalAmount.subtract(currentPaid);

        // Prevent overpayment
        if (request.getAmount().compareTo(
                remainingAmount) > 0) {

            throw new RuntimeException(
                    "Payment amount cannot be greater than "
                            + "remaining invoice amount: "
                            + remainingAmount
            );
        }

        // Check duplicate transaction
        if (request.getTransactionId() != null &&
                !request.getTransactionId()
                        .trim()
                        .isEmpty() &&
                paymentRepository.existsByTransactionId(
                        request.getTransactionId()
                )) {

            throw new RuntimeException(
                    "Transaction ID already exists: "
                            + request.getTransactionId()
            );
        }

        // Check duplicate reference
        if (request.getReferenceNumber() != null &&
                !request.getReferenceNumber()
                        .trim()
                        .isEmpty() &&
                paymentRepository.existsByReferenceNumber(
                        request.getReferenceNumber()
                )) {

            throw new RuntimeException(
                    "Reference number already exists: "
                            + request.getReferenceNumber()
            );
        }

        // =================================================
        // CREATE PAYMENT
        // =================================================

        Payment payment = new Payment();

        payment.setDoctorId(doctorId);

        payment.setInvoiceId(
                invoice.getId()
        );

        payment.setOwnerId(
                request.getOwnerId() != null
                        ? request.getOwnerId()
                        : invoice.getOwnerId()
        );

        payment.setPatientId(
                request.getPatientId() != null
                        ? request.getPatientId()
                        : invoice.getPatientId()
        );

        payment.setAmount(
                request.getAmount()
        );

        payment.setPaymentMethod(
                request.getPaymentMethod() != null &&
                        !request.getPaymentMethod()
                                .trim()
                                .isEmpty()
                        ? request.getPaymentMethod()
                        : "CASH"
        );

        payment.setStatus("SUCCESS");

        payment.setTransactionId(
                request.getTransactionId()
        );

        payment.setReferenceNumber(
                request.getReferenceNumber()
        );

        payment.setPaymentDate(
                request.getPaymentDate() != null
                        ? request.getPaymentDate()
                        : LocalDateTime.now()
        );

        payment.setNotes(
                request.getNotes()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        // =================================================
        // UPDATE INVOICE
        // =================================================

        BigDecimal newPaidAmount =
                currentPaid.add(
                        request.getAmount()
                );

        BigDecimal newDueAmount =
                totalAmount.subtract(
                        newPaidAmount
                );

        invoice.setPaidAmount(
                newPaidAmount
        );

        invoice.setDueAmount(
                newDueAmount
        );

        if (newPaidAmount.compareTo(
                BigDecimal.ZERO) == 0) {

            invoice.setPaymentStatus("UNPAID");

        } else if (newPaidAmount.compareTo(
                totalAmount) >= 0) {

            invoice.setPaymentStatus("PAID");

        } else {

            invoice.setPaymentStatus(
                    "PARTIALLY_PAID"
            );
        }

        invoiceRepository.save(invoice);

        return mapToResponse(savedPayment);
    }

    // =====================================================
    // GET ALL PAYMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Payment payment =
                paymentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(payment);
    }

    // =====================================================
    // GET BY INVOICE
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByInvoiceId(
            Long invoiceId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository
                .findByInvoiceIdAndDoctorId(invoiceId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY OWNER
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByOwnerId(
            Long ownerId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository
                .findByOwnerIdAndDoctorId(ownerId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PATIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByPatientId(
            Long patientId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByStatus(
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository
                .findByStatusAndDoctorId(status, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY PAYMENT METHOD
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByPaymentMethod(
            String paymentMethod) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return paymentRepository
                .findByPaymentMethodAndDoctorId(paymentMethod, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY TRANSACTION ID
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByTransactionId(
            String transactionId) {

        return paymentRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY REFERENCE NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByReferenceNumber(
            String referenceNumber) {

        return paymentRepository
                .findByReferenceNumber(referenceNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // =====================================================

    public PaymentResponse updateStatus(
            Long id,
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Payment payment =
                paymentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found with id: "
                                                + id
                                )
                        );

        payment.setStatus(status);

        Payment updated =
                paymentRepository.save(payment);

        return mapToResponse(updated);
    }

    // =====================================================
    // DELETE PAYMENT
    // =====================================================

    public void deletePayment(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Payment payment =
                paymentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found with id: "
                                                + id
                                )
                        );

        // If successful payment is deleted,
        // recalculate the invoice.
        if ("SUCCESS".equalsIgnoreCase(
                payment.getStatus())) {

            Invoice invoice =
                    invoiceRepository.findByIdAndDoctorId(
                            payment.getInvoiceId(),
                            doctorId
                    ).orElse(null);

            if (invoice != null) {

                BigDecimal currentPaid =
                        invoice.getPaidAmount() != null
                                ? invoice.getPaidAmount()
                                : BigDecimal.ZERO;

                BigDecimal newPaid =
                        currentPaid.subtract(
                                payment.getAmount()
                        );

                if (newPaid.compareTo(
                        BigDecimal.ZERO) < 0) {

                    newPaid = BigDecimal.ZERO;
                }

                invoice.setPaidAmount(newPaid);

                invoice.setDueAmount(
                        invoice.getTotalAmount()
                                .subtract(newPaid)
                );

                if (newPaid.compareTo(
                        BigDecimal.ZERO) == 0) {

                    invoice.setPaymentStatus(
                            "UNPAID"
                    );

                } else if (newPaid.compareTo(
                        invoice.getTotalAmount()) >= 0) {

                    invoice.setPaymentStatus(
                            "PAID"
                    );

                } else {

                    invoice.setPaymentStatus(
                            "PARTIALLY_PAID"
                    );
                }

                invoiceRepository.save(invoice);
            }
        }

        paymentRepository.delete(payment);
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private PaymentResponse mapToResponse(
            Payment payment) {

        PaymentResponse response =
                new PaymentResponse();

        response.setId(
                payment.getId()
        );

        response.setDoctorId(
                payment.getDoctorId()
        );

        response.setInvoiceId(
                payment.getInvoiceId()
        );

        response.setOwnerId(
                payment.getOwnerId()
        );

        response.setPatientId(
                payment.getPatientId()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setPaymentMethod(
                payment.getPaymentMethod()
        );

        response.setStatus(
                payment.getStatus()
        );

        response.setTransactionId(
                payment.getTransactionId()
        );

        response.setReferenceNumber(
                payment.getReferenceNumber()
        );

        response.setPaymentDate(
                payment.getPaymentDate()
        );

        response.setNotes(
                payment.getNotes()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        response.setUpdatedAt(
                payment.getUpdatedAt()
        );

        return response;
    }
}