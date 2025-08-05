package onlinecourseplatform.utility;

import onlinecourseplatform.dto.responseDTOs.UserResponseDTO;
import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Enrollment;
import onlinecourseplatform.entity.Status;
import onlinecourseplatform.entity.User;
import onlinecourseplatform.repository.CourseRepository;
import onlinecourseplatform.repository.EnrollmentRepository;
import onlinecourseplatform.repository.UserRepository;
import onlinecourseplatform.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Utility {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final Conversion conversion;

    /**
     * Find course by course ID.
     */
    public Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
    }

    /**
     * Find user by email.
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * exists user by email.
     */
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user ID from Principal.
     */
    public Long getUserIdFromPrincipal(Principal principal) {
        return findUserByEmail(principal.getName()).getId();
    }

    /**
     * Get user ID from Spring Security authentication.
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new RuntimeException("Cannot extract user ID from authentication");
    }

    /**
     * Validate enrollment of a student in a course.
     */
    public Enrollment validateEnrollment(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

        if (enrollment == null) {
            throw new RuntimeException("You are not enrolled in this course");
        }
        if (Objects.requireNonNull(enrollment.getStatus()) == Status.COMPLETED) {
            throw new RuntimeException("You have already completed the course");
        }
        return enrollment;
    }

    /**
     * Get UserResponseDTO by user ID.
     */
    public UserResponseDTO getUserResponseDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(conversion::toResponseDto)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Check if current user has ADMIN role.
     */
    public boolean isCurrentUserAdmin() {
        return getCurrentUserRole().equals("ADMIN");
    }

    /**
     * Get current user role.
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.startsWith("ROLE_")) {
                return role.substring(5); // Remove "ROLE_" prefix
            }
        }

        throw new RuntimeException("No role found for authenticated user");
    }

    /**
     * Optional: Get full User entity by ID.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * Optional: Check if user is enrolled in a course.
     */
    public boolean isUserEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }
}
