package doctor.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import doctor.backend.dto.payment.PaymentRequest;
import doctor.backend.dto.payment.PaymentResponse;
import doctor.backend.dto.payment.RazorpayOrderRequest;
import doctor.backend.dto.payment.RazorpayOrderResponse;
import doctor.backend.dto.payment.RazorpayVerifyRequest;
import doctor.backend.entity.Invoice;
import doctor.backend.repository.InvoiceRepository;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RazorpayService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentService paymentService;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public RazorpayService(InvoiceRepository invoiceRepository,
                            PaymentService paymentService) {
        this.invoiceRepository = invoiceRepository;
        this.paymentService = paymentService;
    }

    // =====================================================
    // CREATE ORDER
    // =====================================================

    public RazorpayOrderResponse createOrder(RazorpayOrderRequest request) {

        if (request.getInvoiceId() == null) {
            throw new RuntimeException("Invoice ID is required");
        }

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        if ("CANCELLED".equalsIgnoreCase(invoice.getStatus())) {
            throw new RuntimeException("Cannot pay a cancelled invoice");
        }

        BigDecimal totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal due = totalAmount.subtract(paidAmount);

        BigDecimal amount = request.getAmount() != null ? request.getAmount() : due;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        if (amount.compareTo(due) > 0) {
            throw new RuntimeException("Payment amount cannot be greater than remaining invoice amount: " + due);
        }

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            // Razorpay expects the amount in the smallest currency unit (paise for INR).
            long amountInPaise = amount.setScale(2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "invoice-" + invoice.getId());
            options.put("payment_capture", 1);

            Order order = client.orders.create(options);

            return new RazorpayOrderResponse(
                    order.get("id"),
                    keyId,
                    amountInPaise,
                    "INR",
                    invoice.getId()
            );

        } catch (Exception e) {
            throw new RuntimeException("Could not create Razorpay order: " + e.getMessage(), e);
        }
    }

    // =====================================================
    // VERIFY PAYMENT & RECORD IT
    // =====================================================

    public PaymentResponse verifyAndRecordPayment(RazorpayVerifyRequest request) {

        if (request.getInvoiceId() == null
                || request.getRazorpayOrderId() == null
                || request.getRazorpayPaymentId() == null
                || request.getRazorpaySignature() == null) {

            throw new RuntimeException("Missing Razorpay verification fields");
        }

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (!valid) {
                throw new RuntimeException("Payment signature verification failed");
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not verify Razorpay payment: " + e.getMessage(), e);
        }

        BigDecimal totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paidAmount = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal due = totalAmount.subtract(paidAmount);

        // Prefer the amount the order was actually created for; fall back to
        // the full outstanding due amount if the frontend didn't send it.
        BigDecimal amountToRecord = request.getAmount() != null ? request.getAmount() : due;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setInvoiceId(invoice.getId());
        paymentRequest.setOwnerId(invoice.getOwnerId());
        paymentRequest.setPatientId(invoice.getPatientId());
        paymentRequest.setAmount(amountToRecord);
        paymentRequest.setPaymentMethod("RAZORPAY");
        paymentRequest.setTransactionId(request.getRazorpayPaymentId());
        paymentRequest.setReferenceNumber(request.getRazorpayOrderId());
        paymentRequest.setNotes("Paid via Razorpay");

        return paymentService.createPayment(paymentRequest);
    }
}
