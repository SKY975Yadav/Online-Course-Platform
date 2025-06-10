package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.Course;
import com.example.onlinecourseplatform.repository.CourseRepository;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.utilty.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing courses and instructor-related actions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private final Utility utility;

    /**
     * Retrieves all courses available on the platform.
     */
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(utility::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all courses created by a specific instructor.
     */
    public List<CourseDto> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream().map(utility::toDto).collect(Collectors.toList());
    }

    /**
     * Creates a new course using the given CourseDto.
     */
    @Transactional
    public CourseDto createCourse(CourseDto courseDto, Long instructorId) {
        courseDto.setInstructorId(instructorId);
        Course course = utility.toEntity(courseDto);
        course = courseRepository.save(course);
        log.info("Course created by instructor {}: {}", courseDto.getInstructorId(), course.getTitle());
        return utility.toDto(course);
    }

    /**
     * Updates the details of an existing course.
     */
    @Transactional
    public CourseDto updateCourse(Long courseId, CourseDto courseDto, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }

        if (courseDto.getTitle() != null) course.setTitle(courseDto.getTitle());
        if (courseDto.getDescription() != null) course.setDescription(courseDto.getDescription());
        if (courseDto.getPrice() != null) course.setPrice(courseDto.getPrice());

        course = courseRepository.save(course);
        log.info("Instructor {} updated course {}", instructorId, courseId);
        return utility.toDto(course);
    }

    /**
     * Deletes a course by its ID if it exists.
     */
    @Transactional
    public void deleteCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You are not the owner of this course");
        }

        enrollmentService.deleteEnrollmentsByCourseId(courseId); // First delete enrollments
        courseRepository.deleteById(courseId);           // Then delete course
    }

    /**
     * Retrieves course details by its ID.
     */
    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(utility::toDto)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    /**
     * Retrieves a list of students enrolled in a specific course.
     */
    public List<UserDto> getEnrolledStudents(Long courseId, Long instructorId) {
        // 1. Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2. Check if the instructor is the course owner
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You are not the owner of this course");
        }

        // 3. Get enrollments for the course
        List<EnrollmentDto> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);

        // 4. Map each enrollment to a UserDto
        List<UserDto> enrolledStudents = new ArrayList<>();
        for (EnrollmentDto enrollment : enrollments) {
            UserDto userDto = getUserById(enrollment.getStudentId());
            enrolledStudents.add(userDto);
        }

        return enrolledStudents;
    }

    /**
     * Helper method to retrieve a UserDto by user ID.
     */
    private UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(utility::toDto)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
}
