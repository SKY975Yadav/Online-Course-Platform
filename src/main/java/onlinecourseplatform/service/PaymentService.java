package onlinecourseplatform.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Payment;
import onlinecourseplatform.entity.PaymentStatus;
import onlinecourseplatform.entity.User;
import onlinecourseplatform.repository.CourseRepository;
import onlinecourseplatform.repository.PaymentRepository;
import onlinecourseplatform.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private PaymentRepository paymentRepository;

    private UserRepository userRepository;

    private CourseRepository courseRepository;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    public Map<String, Object> createOrder(Double amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject options = new JSONObject();
        options.put("amount", (int)(amount * 100)); // amount in paise
        options.put("currency", "INR");
        options.put("receipt", "rcpt_" + UUID.randomUUID());

        Order order = client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));

        return response;
    }

    // Method to verify payment signature
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, razorpaySecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public void savePayment(String orderId, String paymentId, String signature, Double amount, Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Payment payment = Payment.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .signature(signature)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .paymentMethod("Razorpay")
                .user(user)
                .course(course)
                .build();

        paymentRepository.save(payment);
    }
}