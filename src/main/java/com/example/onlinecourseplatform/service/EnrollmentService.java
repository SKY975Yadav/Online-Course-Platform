package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.EnrollmentBodyOutput;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.entity.Enrollment;
import com.example.onlinecourseplatform.repository.CourseRepository;
import com.example.onlinecourseplatform.utilty.Utility;
import lombok.extern.slf4j.Slf4j;
import com.example.onlinecourseplatform.repository.EnrollmentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class to manage course enrollments for students.
 * Provides operations for enrolling, unenrolling, and retrieving enrollments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final Utility utility;
    /**
     // Returns a list of all enrollments for a specific user.
     */
    public List<EnrollmentDto> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream().map(utility::toDto).collect(Collectors.toList());
    }

    /**
     * Enrolls a student into a course if not already enrolled.
     */
    @Transactional
    public EnrollmentBodyOutput enroll(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student already enrolled in this course");
        }
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .enrolledAt(LocalDateTime.now())
                .build();
        enrollment = enrollmentRepository.save(enrollment);
        log.info("Student {} enrolled in course {}", studentId, courseId);
        CourseDto course = utility.toDto(courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId)));
        return EnrollmentBodyOutput.builder()
                .enrollment(utility.toDto(enrollment))
                .message("Congratulations! You have successfully enrolled in the course: " + course.getTitle()).build();

    }

    /**
     * Returns a list of all enrollments in the platform.
     */
    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream().map(utility::toDto).collect(Collectors.toList());
    }

    /**
     * Unenrolls a user from a course.
     */
    @Transactional
    public void unenroll(Long userId, @NotNull Long courseId) {
        if (!enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId)) {
            throw new IllegalArgumentException("You are not enrolled in this course");
        }
        enrollmentRepository.deleteByStudentIdAndCourseId(userId, courseId);
        log.info("User {} unenrolled from course {}", userId, courseId);
    }

    /**
     * Deletes all enrollments of course, when course is deleted.
     */
    @Transactional
    public void deleteEnrollmentsByCourseId(Long courseId) {
        enrollmentRepository.deleteByCourseId(courseId);
    }

    /**
     * Deletes all enrollments of a specific student, when the student is deleted.
     */
    @Transactional
    public void deleteEnrollmentsByStudentId(Long studentID) {
        enrollmentRepository.deleteByStudentId(studentID);
    }

    /**
     * Retrieves all enrollments for a specific course.
     */
    public List<EnrollmentDto> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        List<EnrollmentDto> enrollmentDtos = new ArrayList<>();
        for (Enrollment enrollment : enrollments){
            enrollmentDtos.add(utility.toDto(enrollment));
        }
        return enrollmentDtos;
    }
}
