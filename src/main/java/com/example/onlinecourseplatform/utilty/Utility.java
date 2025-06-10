package com.example.onlinecourseplatform.utilty;

import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.EnrollmentDto;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.Course;
import com.example.onlinecourseplatform.entity.Enrollment;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class Utility {
    private final UserRepository userRepository;

    // 🔐 Helper to extract user ID from Principal
    public Long getUserIdFromPrincipal(Principal principal) {
        UserDto user = toDto(userRepository.findByEmail(principal.getName()).orElseThrow());
        return user.getId();
    }

    /**
     * Converts an Enrollment entity to its corresponding DTO.
     */
    public EnrollmentDto toDto(Enrollment enrollment) {
        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }


    /**
     * Converts a Course entity to a CourseDto.
     */
    public CourseDto toDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructorId())
                .price(course.getPrice())
                .build();
    }

    /**
     * Converts a CourseDto to a Course entity.
     */
    public Course toEntity(CourseDto dto) {
        return Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .instructorId(dto.getInstructorId())
                .price(dto.getPrice())
                .build();
    }

    /**
     * Converts a User entity to a UserDto.
     */
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }

    /**
     * Converts a UserDto to a User entity.
     */
    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .build();
    }

}
