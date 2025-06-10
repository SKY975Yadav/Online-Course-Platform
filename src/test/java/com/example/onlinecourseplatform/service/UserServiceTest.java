package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.ChangePasswordRequest;
import com.example.onlinecourseplatform.dto.UpdateUserRequest;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.entity.Role;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.utilty.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private Utility utility;

    @InjectMocks
    private UserService userService;

    private final String EMAIL = "test@example.com";

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email(EMAIL)
                .password("encodedPass")
                .role(Role.STUDENT)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email(EMAIL)
                .password("encodedPass")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void findByEmail_userExists_returnsUserDto() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(utility.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findByEmail(EMAIL);

        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void getAllUsers_returnsUserDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(utility.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(userDto.getEmail(), result.getFirst().getEmail());
    }

    @Test
    void deleteUser_studentRole_callsDeleteStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(enrollmentService).deleteEnrollmentsByStudentId(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_adminRole_throwsException() {
        User adminUser = User.builder().role(Role.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("Cannot delete admin user", exception.getMessage());
    }

    @Test
    void updateUser_savesAndReturnsDto() {
        User updatedUser = User.builder().password("newEncoded").build();

        when(utility.toEntity(userDto)).thenReturn(updatedUser);
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("newEncoded");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(utility.toDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(userDto);

        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserDetails_correctPassword_updatesUser() {
        UpdateUserRequest updateRequest = new UpdateUserRequest("New Name", "new@example.com", "correct");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(utility.toDto(user)).thenReturn(userDto);
        when(passwordEncoder.matches("correct", userDto.getPassword())).thenReturn(true);
        when(utility.toEntity(any(UserDto.class))).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(utility.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateUserDetails(updateRequest, EMAIL);

        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void updateUserDetails_wrongPassword_throwsSecurityException() {
        UpdateUserRequest updateRequest = new UpdateUserRequest("New Name", "new@example.com", "wrong");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(utility.toDto(user)).thenReturn(userDto);
        when(passwordEncoder.matches("wrong", userDto.getPassword())).thenReturn(false);

        assertThrows(SecurityException.class, () -> userService.updateUserDetails(updateRequest, EMAIL));
    }

    @Test
    void changePassword_correctPassword_updatesPassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("current", "newPass", "newPass");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(utility.toDto(user)).thenReturn(userDto);
        when(passwordEncoder.matches("current", userDto.getPassword())).thenReturn(true);
        when(utility.toEntity(any(UserDto.class))).thenReturn(user);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(utility.toDto(any(User.class))).thenReturn(userDto);

        assertDoesNotThrow(() -> userService.changePassword(changePasswordRequest, EMAIL));
    }

    @Test
    void changePassword_wrongCurrentPassword_throwsSecurityException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("wrong", "newPass", "newPass");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(utility.toDto(user)).thenReturn(userDto);
        when(passwordEncoder.matches("wrong", userDto.getPassword())).thenReturn(false);

        assertThrows(SecurityException.class, () -> userService.changePassword(changePasswordRequest, EMAIL));
    }

    @Test
    void changePassword_mismatchNewPasswords_throwsIllegalArgumentException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("current", "newPass", "wrongConfirm");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword(changePasswordRequest, EMAIL));

        assertEquals("New password and confirm password do not match", exception.getMessage());
    }
}
