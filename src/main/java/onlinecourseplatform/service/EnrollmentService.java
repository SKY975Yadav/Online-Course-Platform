package onlinecourseplatform.service;

import onlinecourseplatform.dto.responseDTOs.BasicCourseDetailsResponse;
import onlinecourseplatform.dto.responseDTOs.EnrollmentResponseDTO;
import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Enrollment;
import onlinecourseplatform.entity.Status;
import onlinecourseplatform.repository.CourseRepository;
import onlinecourseplatform.utility.Conversion;
import onlinecourseplatform.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import onlinecourseplatform.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class to manage course enrollments for students.
 * Provides operations for enrolling, unenrolling, and retrieving enrollments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final Conversion conversion;
    private final Utility utility;

    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream().map(conversion::toResponseDto).toList();
    }

    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findAllByStudentId(studentId)
                .stream().map(conversion::toResponseDto).toList();
    }

    public List<BasicCourseDetailsResponse> getCoursesByStudent(Long studentId) {
        return enrollmentRepository.findCoursesByStudentId(studentId)
                .stream().map(conversion::toBasicCourseDto).toList();
    }

    @Transactional
    public EnrollmentResponseDTO enroll(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Student {} is already enrolled in course {}", studentId, courseId);
            throw new RuntimeException("Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .course(course)
                .price(course.getPrice())
                .enrolledAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Student {} enrolled in course {}", studentId, courseId);
        return conversion.toResponseDto(saved);
    }

    @Transactional
    public void courseCompleted(Long studentId, Long courseId) {
        Enrollment enrollment = utility.validateEnrollment(studentId, courseId);
        enrollment.setCompletedAt(LocalDateTime.now());
        enrollment.setStatus(Status.COMPLETED);
        enrollmentRepository.save(enrollment);
        log.info("Student {} completed course {}", studentId, courseId);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}
