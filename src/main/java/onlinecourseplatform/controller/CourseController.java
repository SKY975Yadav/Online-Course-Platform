package onlinecourseplatform.controller;

import onlinecourseplatform.dto.requestDTOs.CourseRequestDTO;
import onlinecourseplatform.dto.requestDTOs.CourseUpdateRequest;
import onlinecourseplatform.dto.responseDTOs.BasicCourseDetailsResponse;
import onlinecourseplatform.dto.responseDTOs.CourseResponseDTO;
import onlinecourseplatform.dto.responseDTOs.UserResponseDTO;
import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Role;
import onlinecourseplatform.service.CourseService;
import onlinecourseplatform.utility.Conversion;
import onlinecourseplatform.utility.Utility;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Retrieve all courses.
     */
    @Operation(summary = "Get all courses")
    @GetMapping("/all")
    public ResponseEntity<List<BasicCourseDetailsResponse>> getAllCourses() {
        log.info("Fetching all courses)");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Get course details by ID (role-based response)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, Principal principal) {
        log.info("Fetching course details for ID {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // For unauthenticated users
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(courseService.getCourseForStudent(id));
        }

        // Get role
        Role userRole = Role.valueOf(utility.getCurrentUserRole());

        // For instructors or admins
        if (userRole == Role.INSTRUCTOR || userRole == Role.ADMIN) {
            Course course = courseService.getCourseEntityById(id); // New helper method to return entity
            Long currentUserId = utility.getUserIdFromPrincipal(principal);

            // Allow full access only to course's instructor or an admin
            if (userRole == Role.ADMIN || course.getInstructorId().equals(currentUserId)) {
                return ResponseEntity.ok(courseService.getFullCourseForInstructor(id));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to view full course details of this course.");
            }
        }

        // For students
        return ResponseEntity.ok(courseService.getCourseForStudent(id));
    }


    /**
     * Get all courses created by the logged-in instructor.
     */
    @Operation(summary = "Get instructor's courses")
    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseResponseDTO>> getInstructorCourses(Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        log.info("Fetching courses for instructor ID {}", instructorId);
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    /**
     * Create a course (instructors only).
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO courseDto, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        CourseResponseDTO created = courseService.createCourse(courseDto, instructorId);
        log.info("Instructor {} created course: {}", instructorId, created.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing course (instructors only).
     */
    @Operation(summary = "Update course details (Instructor only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateRequest courseDto, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        CourseResponseDTO updated = courseService.updateCourse(id, courseDto, instructorId);
        log.info("Instructor {} updated course ID {}", instructorId, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a course (instructors and admins).
     */
    @Operation(summary = "Delete a course (Instructor or Admin)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, Principal principal) {
        Long userId = utility.getUserIdFromPrincipal(principal);
        boolean isAdmin = utility.isCurrentUserAdmin(); // <-- Check if current user is admin
        courseService.deleteCourse(id, userId, isAdmin);
        log.info("{} deleted course ID {}", isAdmin ? "Admin" : "Instructor", id);
        return ResponseEntity.ok("Course deleted successfully");
    }


    /**
     * Get students enrolled in a course (instructors only).
     */
    @Operation(summary = "Get students enrolled in a course (Instructor only)")
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getEnrolledStudents(@PathVariable Long id, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        log.info("Instructor {} fetching students for course ID {}", instructorId, id);
        List<UserResponseDTO> students = courseService.getEnrolledStudents(id, instructorId);
        return ResponseEntity.ok(students);
    }

    /**
     * Get enrolled students count in a course (instructors only).
     */
    @Operation(summary = "Get enrolled students count in a course (Instructor only)")
    @GetMapping("/{id}/students-count")
    public ResponseEntity<Integer> getEnrolledStudentsCount(@PathVariable Long id, Principal principal) {
        Long instructorId = utility.getUserIdFromPrincipal(principal);
        log.info("Fetching students Count for course ID {}", id);
        List<UserResponseDTO> students = courseService.getEnrolledStudents(id, instructorId);
        return ResponseEntity.ok(students.size());
    }

    /**
     *  Get Course Content to enrolled Students
     */
    @Operation(summary = "Get Course content to enrolled Students")
    @GetMapping("/{id}/course-content")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getCourseContent(@PathVariable Long id,Principal principal){
        Long studentId = utility.getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(courseService.getCourseContent(id,studentId));
    }

    /**
     * Get popular courses with pagination.
     */
    @GetMapping("/popular")
    public ResponseEntity<List<BasicCourseDetailsResponse>> getPopularCourses(@RequestParam(defaultValue = "5") int limit) {
        log.info("Fetching top {} popular courses", limit);
        List<BasicCourseDetailsResponse> results = courseService.getPopularCourses(limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Search courses by keyword.
     */
    @GetMapping("/search")
    public ResponseEntity<List<BasicCourseDetailsResponse>> searchCourses(@RequestParam String query) {
        log.info("Searching courses with keyword: {}", query);
        List<BasicCourseDetailsResponse> results = courseService.searchCourses(query);
        return ResponseEntity.ok(results);
    }
}
