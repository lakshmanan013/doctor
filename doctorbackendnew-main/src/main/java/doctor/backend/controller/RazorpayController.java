package doctor.backend.controller;

import doctor.backend.dto.payment.PaymentResponse;
import doctor.backend.dto.payment.RazorpayOrderRequest;
import doctor.backend.dto.payment.RazorpayOrderResponse;
import doctor.backend.dto.payment.RazorpayVerifyRequest;
import doctor.backend.service.RazorpayService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/razorpay")
@CrossOrigin(origins = "*")
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    // =====================================================
    // CREATE ORDER
    // POST /api/payments/razorpay/order
    // =====================================================

    @PostMapping("/order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(
            @RequestBody RazorpayOrderRequest request) {

        return ResponseEntity.ok(razorpayService.createOrder(request));
    }

    // =====================================================
    // VERIFY PAYMENT
    // POST /api/payments/razorpay/verify
    // =====================================================

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @RequestBody RazorpayVerifyRequest request) {

        return ResponseEntity.ok(razorpayService.verifyAndRecordPayment(request));
    }
}
