package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.Course;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.repository.CourseRepository;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.utilty.Utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Utility utility;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCourses() {
        Course course = new Course();
        CourseDto courseDto = new CourseDto();
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(utility.toDto(course)).thenReturn(courseDto);

        List<CourseDto> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        verify(courseRepository).findAll();
    }

    @Test
    void testGetCoursesByInstructor() {
        Long instructorId = 1L;
        Course course = new Course();
        CourseDto courseDto = new CourseDto();
        when(courseRepository.findByInstructorId(instructorId)).thenReturn(List.of(course));
        when(utility.toDto(course)).thenReturn(courseDto);

        List<CourseDto> result = courseService.getCoursesByInstructor(instructorId);

        assertEquals(1, result.size());
        verify(courseRepository).findByInstructorId(instructorId);
    }

    @Test
    void testCreateCourse() {
        Long instructorId = 1L;
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Java");
        Course course = new Course();

        when(utility.toEntity((CourseDto) any())).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(utility.toDto(course)).thenReturn(courseDto);

        CourseDto result = courseService.createCourse(courseDto, instructorId);

        assertEquals("Java", result.getTitle());
        verify(courseRepository).save(course);
    }

    @Test
    void testUpdateCourse_Success() {
        Long courseId = 1L;
        Long instructorId = 2L;
        Course course = new Course();
        course.setInstructorId(instructorId);
        CourseDto updateDto = new CourseDto();
        updateDto.setTitle("New Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);
        when(utility.toDto(course)).thenReturn(updateDto);

        CourseDto result = courseService.updateCourse(courseId, updateDto, instructorId);

        assertEquals("New Title", result.getTitle());
        verify(courseRepository).save(course);
    }

    @Test
    void testUpdateCourse_AccessDenied() {
        Long courseId = 1L;
        Long instructorId = 2L;
        Course course = new Course();
        course.setInstructorId(99L);
        CourseDto updateDto = new CourseDto();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class,
                () -> courseService.updateCourse(courseId, updateDto, instructorId));
    }

    @Test
    void testDeleteCourse_Success() {
        Long courseId = 1L;
        Long instructorId = 10L;
        Course course = new Course();
        course.setInstructorId(instructorId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.deleteCourse(courseId, instructorId);

        verify(enrollmentService).deleteEnrollmentsByCourseId(courseId);
        verify(courseRepository).deleteById(courseId);
    }

    @Test
    void testDeleteCourse_AccessDenied() {
        Long courseId = 1L;
        Long instructorId = 5L;
        Course course = new Course();
        course.setInstructorId(9L);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class,
                () -> courseService.deleteCourse(courseId, instructorId));
    }

    @Test
    void testGetCourseById_Success() {
        Long id = 1L;
        Course course = new Course();
        CourseDto courseDto = new CourseDto();

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(utility.toDto(course)).thenReturn(courseDto);

        CourseDto result = courseService.getCourseById(id);

        assertNotNull(result);
        verify(courseRepository).findById(id);
    }

    @Test
    void testGetCourseById_NotFound() {
        Long id = 1L;
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getCourseById(id));
    }

    @Test
    void testGetEnrolledStudents_Success() {
        Long courseId = 1L;
        Long instructorId = 2L;

        Course course = new Course();
        course.setInstructorId(instructorId);

        EnrollmentDto enrollment1 = new EnrollmentDto();
        enrollment1.setStudentId(100L);

        UserDto userDto = new UserDto();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentService.getEnrollmentsByCourse(courseId)).thenReturn(List.of(enrollment1));
        when(userRepository.findById(100L)).thenReturn(Optional.of(new User()));
        when(utility.toDto((User) any())).thenReturn(userDto);

        List<UserDto> result = courseService.getEnrolledStudents(courseId, instructorId);

        assertEquals(1, result.size());
        verify(enrollmentService).getEnrollmentsByCourse(courseId);
    }

    @Test
    void testGetEnrolledStudents_AccessDenied() {
        Long courseId = 1L;
        Long instructorId = 999L;

        Course course = new Course();
        course.setInstructorId(1L);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class, () ->
                courseService.getEnrolledStudents(courseId, instructorId));
    }
}
