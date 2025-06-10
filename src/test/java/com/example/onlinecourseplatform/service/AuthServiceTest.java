package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.Role;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.security.JwtUtil;
import com.example.onlinecourseplatform.utilty.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Utility utility;

    @Mock
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccessWithNewToken() {
        String email = "test@example.com";
        String password = "password";
        Long userId = 1L;
        String generatedToken = "fake-jwt-token";

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName("Test");
        userDto.setRole(Role.STUDENT);
        userDto.setId(userId);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(userDto);
        when(redisService.getToken(userId)).thenReturn(null);  // No token exists
        when(jwtUtil.generateToken(email)).thenReturn(generatedToken);

        ResponseEntity<?> response = authService.login(email, password);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(generatedToken, body.get("token"));
        assertEquals("Login successful", body.get("message"));

        verify(redisService).saveToken(userId, generatedToken);
    }

    @Test
    void testLoginSuccessWithExistingToken() {
        String email = "test@example.com";
        String password = "password";
        Long userId = 1L;
        String existingToken = "existing-token";

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName("Test");
        userDto.setRole(Role.STUDENT);
        userDto.setId(userId);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(userDto);
        when(redisService.getToken(userId)).thenReturn(existingToken); // Token exists

        ResponseEntity<?> response = authService.login(email, password);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(existingToken, body.get("token"));
        assertEquals("User already logged in, token refreshed", body.get("message"));

        verify(redisService, never()).saveToken(any(), any()); // No new token saved
    }

    @Test
    void testLoginFailure() {
        String email = "wrong@example.com";
        String password = "wrong";

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        ResponseEntity<?> response = authService.login(email, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void testLogout() {
        String email = "logout@example.com";
        Long userId = 10L;

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setId(userId);

        when(userService.findByEmail(email)).thenReturn(userDto);
        when(redisService.getToken(userId)).thenReturn("some-token");

        ResponseEntity<?> response = authService.logout(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(redisService).deleteToken(userId);
    }

    @Test
    void testRegisterSuccess() {
        UserDto userDto = new UserDto();
        userDto.setEmail("new@example.com");
        userDto.setName("New");
        userDto.setPassword("pass123");
        userDto.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(utility.toDto(any(User.class))).thenReturn(userDto);

        ResponseEntity<?> response = authService.register(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userRepository).save(any());
    }

    @Test
    void testRegisterWithExistingEmail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("existing@example.com");
        userDto.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(userDto));
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void testRegisterAsAdminShouldFail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("admin@example.com");
        userDto.setRole(Role.ADMIN);

        ResponseEntity<?> response = authService.register(userDto);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Admin role assignment not allowed", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void testForgotPasswordGeneratesOtp() {
        String email = "otp@example.com";
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(userDto);

        authService.forgotPassword(email);

        assertTrue(authService.verifyOtp(email, authService.getOtpStorage().get(email)));
    }

    @Test
    void testVerifyOtpValid() {
        String email = "otp@example.com";
        authService.getOtpStorage().put(email, "123456");
        assertTrue(authService.verifyOtp(email, "123456"));
    }

    @Test
    void testVerifyOtpInvalid() {
        String email = "otp@example.com";
        authService.getOtpStorage().put(email, "123456");
        assertFalse(authService.verifyOtp(email, "654321"));
    }

    @Test
    void testResetPasswordSuccess() {
        String email = "reset@example.com";
        String otp = "123456";
        String newPassword = "newPass";

        authService.getOtpStorage().put(email, otp);

        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(userDto);

        authService.resetPassword(email, otp, newPassword);

        assertEquals(newPassword, userDto.getPassword());
        verify(userService).updateUser(userDto);
        assertFalse(authService.getOtpStorage().containsKey(email));
    }

    @Test
    void testResetPasswordWithInvalidOtp() {
        String email = "reset@example.com";
        String otp = "wrong";

        authService.getOtpStorage().put(email, "123456");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPassword(email, otp, "newPass"));

        assertEquals("Invalid OTP", ex.getMessage());
    }
}
