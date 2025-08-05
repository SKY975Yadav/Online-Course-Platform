package onlinecourseplatform.repository;

import onlinecourseplatform.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
    List<Feedback> findByCourseId(Long courseId);
}
