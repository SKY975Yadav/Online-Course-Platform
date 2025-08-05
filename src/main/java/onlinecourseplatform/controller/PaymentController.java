package onlinecourseplatform.controller;

import com.razorpay.RazorpayException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import onlinecourseplatform.dto.requestDTOs.PaymentRequest;
import onlinecourseplatform.entity.Payment;
import onlinecourseplatform.repository.PaymentRepository;
import onlinecourseplatform.service.PaymentService;
import onlinecourseplatform.utility.Utility;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final Utility utility;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest request) {
        try {
            Map<String, Object> order = paymentService.createOrder(request.getAmount());
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Order creation failed.");
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndSavePayment(@RequestBody Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");
        Long courseId = Long.parseLong(data.get("courseId"));
        Long userId = Long.parseLong(data.get("userId"));
        Double amount = Double.parseDouble(data.get("amount"));

        boolean isValid = paymentService.verifyPaymentSignature(orderId, paymentId, signature);

        if (isValid) {
            paymentService.savePayment(orderId, paymentId, signature, amount, userId, courseId);
            return ResponseEntity.ok("Payment verified and saved");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment signature");
        }
    }
    @GetMapping("/my")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Principal principal) {
        Long userId = utility.getUserIdFromPrincipal(principal);
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return ResponseEntity.ok(payments);
    }
}

