package com.example.onlinecourseplatform.controller;

import com.example.onlinecourseplatform.dto.EnrollmentBodyOutput;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.service.EnrollmentService;
import com.example.onlinecourseplatform.utilty.Utility;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

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

    /**
     * Get all enrollments (ADMIN only).
     */
    @Operation(summary = "Get all enrollments (Admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() {
        log.info("Admin requested all enrollments");
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    /**
     * Enroll a student in a course.
     */
    @Operation(summary = "Enroll student in a course")
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentBodyOutput> enroll(@PathVariable @NotNull Long courseId, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        log.info("Student {} attempting to enroll in course {}", studentId, courseId);
        EnrollmentBodyOutput enrollmentBodyOutput = enrollmentService.enroll(studentId, courseId);
        log.info("Student {} enrolled in course {}", studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentBodyOutput);
    }

    /**
     * Get all courses the student is enrolled in.
     */

    @Operation(summary = "Get all courses student is enrolled in")
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentDto>> getMyEnrollments(Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        log.info("Fetching enrollments for student {}", studentId);
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    /**
     * Unenroll a student from a course.
     */

    @Operation(summary = "Unenroll from a course")
    @DeleteMapping("/unenroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> unenroll(@PathVariable @NotNull Long courseId, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        enrollmentService.unenroll(studentId, courseId);
        return ResponseEntity.ok("Unenrolled successfully");
    }

}