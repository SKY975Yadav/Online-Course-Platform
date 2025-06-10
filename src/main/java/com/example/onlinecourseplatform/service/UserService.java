package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.ChangePasswordRequest;
import com.example.onlinecourseplatform.dto.CourseDto;
import com.example.onlinecourseplatform.dto.UpdateUserRequest;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.utilty.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for managing user registration, updates, and deletion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final Utility utility;

    /**
     * Retrieves a user based on their email address.
     */
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return utility.toDto(user);
    }

    /**
     * Returns a list of all registered users.
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(utility::toDto).collect(Collectors.toList());
    }

    /**
     * Deletes a user by ID if they exist.
     */
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new RuntimeException("User not found with ID: " + id);

        User user = optionalUser.get();
        switch (user.getRole()){
            case STUDENT -> {
                log.info("Deleting student with ID: {}", id);
                deleteStudent(id);
            }
            case INSTRUCTOR -> {
                log.info("Deleting instructor with ID: {}", id);
                deleteInstructor(id);
            }
            case ADMIN -> {
                log.warn("Cannot delete admin user with ID: {}", id);
                throw new RuntimeException("Cannot delete admin user");
            }
            default -> log.info("Deleting user with ID: {}", id);
        }

    }

    /**
     * Deletes a student by ID, including their enrollments.
     */

    @Transactional
    private void deleteStudent(Long studentId) {
        // Delete enrollments
        enrollmentService.deleteEnrollmentsByStudentId(studentId);
        userRepository.deleteById(studentId);
        log.info("Student User deleted: id={}", studentId);
    }

    /**
     * Deletes an instructor by ID, including their enrollments and courses.
     */

    @Transactional
    private void deleteInstructor(Long instructorId) {
        List<CourseDto> courses = courseService.getCoursesByInstructor(instructorId);

        for (CourseDto course : courses) {
            enrollmentService.deleteEnrollmentsByCourseId(course.getId()); // delete enrollments for each course
            courseService.deleteCourse(course.getId(),instructorId); // delete all instructor's courses
        }

        userRepository.deleteById(instructorId);
        log.info("Instructor User deleted: id={}", instructorId);
    }

    /**
     * Updates user details.
     */
    public UserDto updateUser(UserDto userDto) {
        User updatedUser = utility.toEntity(userDto);
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Ensure password is encoded
        updatedUser = userRepository.save(updatedUser);
        log.info("User updated: id={}", userDto.getId());
        return utility.toDto(updatedUser);
    }

    /**
     * Updates user details such as name and email, verifying the current password.
     */

    public UserDto updateUserDetails(UpdateUserRequest updateRequest, String email) {
        UserDto user = findByEmail(email);

        // Password verification
        if (!passwordEncoder.matches(updateRequest.getPassword(), user.getPassword())) {
            log.warn("Update failed for {}: incorrect password", user.getEmail());
            throw new SecurityException("Incorrect password");
        }

        // Update fields
        log.info("Updating user info for {}", user.getEmail());
        if (Objects.nonNull(updateRequest.getName())) {
            user.setName(updateRequest.getName());
        }
        if (Objects.nonNull(updateRequest.getEmail())){
            user.setEmail(updateRequest.getEmail());
        }
        return updateUser(user);
    }

    /**
     * Changes the password for a user.
     */
    public void changePassword(ChangePasswordRequest request, String email) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        UserDto user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new SecurityException("Incorrect current password");
        }

        System.out.println(request.getNewPassword());
        user.setPassword(request.getNewPassword());
        updateUser(user);
    }

}
