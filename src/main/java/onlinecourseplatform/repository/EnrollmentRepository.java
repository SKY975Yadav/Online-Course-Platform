package onlinecourseplatform.repository;

import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Enrollment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long userId, Long courseId);

    Enrollment findByStudentIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findAllByStudentId(Long studentId);

    @Query("SELECT e.course FROM Enrollment e WHERE e.studentId = :studentId")
    List<Course> findCoursesByStudentId(Long studentId);

    @Query("SELECT e.course FROM Enrollment e GROUP BY e.course ORDER BY COUNT(e.id) DESC")
    List<Course> findMostPopularCourses(Pageable pageable);


}

