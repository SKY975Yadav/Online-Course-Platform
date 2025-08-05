package onlinecourseplatform.repository;

import onlinecourseplatform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Payment findByUserIdAndCourseId(Long userId, Long courseId);

    List<Payment> findByUserId(Long userId);
}