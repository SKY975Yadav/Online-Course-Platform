package com.example.onlinecourseplatform.controller;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.service.CourseService;
import com.example.onlinecourseplatform.utilty.Utility;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

/**
 * Handles operations related to course management.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final Utility utility;

    /**
     * Retrieve all courses (admin only).
     */
    @Operation(summary = "Get all courses (Admin only)")
    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        log.info("Fetching all courses (ADMIN)");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Get course details by ID (public access).
     */
    @Operation(summary = "Get course details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        log.info("Fetching course details for ID {}", id);
        CourseDto course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(course);
    }

    /**
     * Get all courses created by the logged-in instructor.
     */

    @Operation(summary = "Get instructor's courses")
    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseDto>> getInstructorCourses(Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        log.info("Fetching courses for instructor ID {}", instructorId);
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    /**
     * Create a course (instructors only).
     */
    @Operation(summary = "Create a new course (Instructor only)")
    @PostMapping("/create")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        CourseDto created = courseService.createCourse(courseDto,instructorId);
        log.info("Instructor {} created course: {}", instructorId, created.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing course (instructors only).
     */
    @Operation(summary = "Update course details (Instructor only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDto courseDto, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        CourseDto updated = courseService.updateCourse(id, courseDto, instructorId);
        log.info("Instructor {} updated course ID {}", instructorId, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a course (instructors only).
     */
    @Operation(summary = "Delete a course (Instructor only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        courseService.deleteCourse(id, instructorId);
        log.info("Instructor {} deleted course ID {}", instructorId, id);
        return ResponseEntity.ok("Course deleted successfully");
    }

    /**
     * Get students enrolled in a course (instructors only).
     */
    @Operation(summary = "Get students enrolled in a course (Instructor only)")
    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<UserDto>> getEnrolledStudents(@PathVariable Long id, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        log.info("Instructor {} fetching students for course ID {}", instructorId, id);
        List<UserDto> students = courseService.getEnrolledStudents(id, instructorId);
        return ResponseEntity.ok(students);
    }
}
