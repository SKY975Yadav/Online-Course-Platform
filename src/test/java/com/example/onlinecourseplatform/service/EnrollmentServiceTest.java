package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.EnrollmentBodyOutput;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.entity.Enrollment;
import com.example.onlinecourseplatform.entity.Course;
import com.example.onlinecourseplatform.repository.CourseRepository;
import com.example.onlinecourseplatform.repository.EnrollmentRepository;
import com.example.onlinecourseplatform.utilty.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private Utility utility;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEnrollmentsByStudent() {
        Long studentId = 1L;
        Enrollment enrollment = Enrollment.builder().studentId(studentId).courseId(2L).build();
        EnrollmentDto dto = new EnrollmentDto();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(utility.toDto(enrollment)).thenReturn(dto);

        List<EnrollmentDto> result = enrollmentService.getEnrollmentsByStudent(studentId);
        assertEquals(1, result.size());
        verify(enrollmentRepository).findByStudentId(studentId);
    }

    @Test
    void testEnroll_Successful() {
        Long studentId = 1L;
        Long courseId = 2L;

        // Dummy enrollment entity
        Enrollment savedEnrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .enrolledAt(LocalDateTime.now())
                .build();

        // DTOs to be returned by utility
        CourseDto courseDto = CourseDto.builder().id(courseId).title("Java Basics").build();
        EnrollmentDto enrollmentDto = new EnrollmentDto();

        // Mocking repository and utility behavior
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(enrollmentRepository.save(any())).thenReturn(savedEnrollment);

        // Create a mock course entity
        Course courseEntity = mock(Course.class);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseEntity));

        // Mock conversion from entity to DTO
        when(utility.toDto(any(Enrollment.class))).thenReturn(enrollmentDto);
        when(utility.toDto(any(Course.class))).thenReturn(courseDto);

        // Act: Call the method under test
        EnrollmentBodyOutput result = enrollmentService.enroll(studentId, courseId);

        // Assert
        assertNotNull(result);
        assertEquals("Congratulations! You have successfully enrolled in the course: Java Basics", result.getMessage());

        // Verify save was called
        verify(enrollmentRepository).save(any());
    }

    @Test
    void testEnroll_AlreadyEnrolled() {
        Long studentId = 1L;
        Long courseId = 2L;

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                enrollmentService.enroll(studentId, courseId));
        assertEquals("Student already enrolled in this course", exception.getMessage());
    }

    @Test
    void testGetAllEnrollments() {
        Enrollment enrollment = new Enrollment();
        EnrollmentDto dto = new EnrollmentDto();

        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment));
        when(utility.toDto(enrollment)).thenReturn(dto);

        List<EnrollmentDto> result = enrollmentService.getAllEnrollments();

        assertEquals(1, result.size());
        verify(enrollmentRepository).findAll();
    }

    @Test
    void testUnenroll_Success() {
        Long studentId = 1L;
        Long courseId = 2L;

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        enrollmentService.unenroll(studentId, courseId);

        verify(enrollmentRepository).deleteByStudentIdAndCourseId(studentId, courseId);
    }

    @Test
    void testUnenroll_NotEnrolled() {
        Long studentId = 1L;
        Long courseId = 2L;

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.unenroll(studentId, courseId));
    }

    @Test
    void testDeleteEnrollmentsByCourseId() {
        Long courseId = 1L;
        enrollmentService.deleteEnrollmentsByCourseId(courseId);
        verify(enrollmentRepository).deleteByCourseId(courseId);
    }

    @Test
    void testDeleteEnrollmentsByStudentId() {
        Long studentId = 1L;
        enrollmentService.deleteEnrollmentsByStudentId(studentId);
        verify(enrollmentRepository).deleteByStudentId(studentId);
    }

    @Test
    void testGetEnrollmentsByCourse() {
        Long courseId = 1L;
        Enrollment enrollment = new Enrollment();
        EnrollmentDto dto = new EnrollmentDto();

        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(List.of(enrollment));
        when(utility.toDto(enrollment)).thenReturn(dto);

        List<EnrollmentDto> result = enrollmentService.getEnrollmentsByCourse(courseId);
        assertEquals(1, result.size());
    }
}
