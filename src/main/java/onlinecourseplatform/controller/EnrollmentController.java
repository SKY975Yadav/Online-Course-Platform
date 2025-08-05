package onlinecourseplatform.controller;

import onlinecourseplatform.dto.responseDTOs.BasicCourseDetailsResponse;
import onlinecourseplatform.dto.responseDTOs.EnrollmentResponseDTO;
import onlinecourseplatform.service.EnrollmentService;
import onlinecourseplatform.utility.Utility;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Handles enrollment-related operations for students and admin.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final Utility utility;

    @GetMapping("all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all enrollments (Admin only)")
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's enrollments")
    public ResponseEntity<List<EnrollmentResponseDTO>> getMyEnrollments(Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get courses enrolled by current student")
    public ResponseEntity<List<BasicCourseDetailsResponse>> getMyCourses(Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(enrollmentService.getCoursesByStudent(studentId));
    }

    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<?> enroll(@PathVariable Long courseId, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        EnrollmentResponseDTO dto = enrollmentService.enroll(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Enrolled successfully",
                "enrollment", dto
        ));
    }


    @PutMapping("/completed/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark course as completed")
    public ResponseEntity<?> courseCompleted(@PathVariable Long courseId, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        enrollmentService.courseCompleted(studentId, courseId);
        return ResponseEntity.ok(Map.of("message", "Course marked as completed"));
    }

    @GetMapping("/{courseId}/is-enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Check enrollment status")
    public ResponseEntity<Boolean> isEnrolled(@PathVariable Long courseId, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(enrollmentService.isEnrolled(studentId, courseId));
    }
}
