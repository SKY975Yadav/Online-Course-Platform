package com.example.onlinecourseplatform.service;

import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.entity.Role;
import com.example.onlinecourseplatform.entity.User;
import com.example.onlinecourseplatform.repository.UserRepository;
import com.example.onlinecourseplatform.security.JwtUtil;
import com.example.onlinecourseplatform.utilty.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service class that handles authentication-related operations such as login,
 * registration, OTP-based password reset, and user verification.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Utility utility;
    private final RedisService redisService;

    // Temporary in-memory OTP storage
    @Getter
    private final Map<String, String> otpStorage = new HashMap<>();

    /**
     * Authenticates a user and generates a JWT token if successful.
     * Returns user details along with the token.
     */
    public ResponseEntity<?> login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, password
                    )
            );

            UserDto userDto = userService.findByEmail(email);
            String message,token;
            String existingToken = redisService.getToken(userDto.getId());
            if (existingToken != null) {
                token = redisService.getToken(userDto.getId());
                message = "User already logged in, token refreshed";
            } else {
                token = jwtUtil.generateToken(email);
                redisService.saveToken(userDto.getId(), token);
                log.info("Login successful for user: {}", email);
                message = "Login successful";
            }

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", userDto.getRole(),
                    "name", userDto.getName(),
                    "message", message
            ));
        } catch (AuthenticationException e) {
            log.warn("Login failed for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    /**
     * Registers a new user if the email is not already taken.
     * Returns a success message with user details or an error if the email is already registered.
     */
    public ResponseEntity<?> register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userDto.getRole() == Role.ADMIN) {
            log.warn("Attempt to register with ADMIN role: {}", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Admin role assignment not allowed"));
        }
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .build();

        UserDto savedUser = utility.toDto(userRepository.save(user));

        log.info("User registered successfully: {}", savedUser.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "name", savedUser.getName(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole()
            ));
    }

    /**
     * Logs out the user by deleting their token from Redis.
     * Returns a success message.
     */
    public ResponseEntity<?> logout(String email) {
        System.out.println("Logging out user: " + email);
        UserDto userDto = userService.findByEmail(email);
        System.out.println(redisService.getToken(userDto.getId()));
        redisService.deleteToken(userDto.getId());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Initiates the password reset process by generating a 6-digit OTP and sending it to the user's email.
     * The OTP is stored temporarily in memory for verification.
     */
    public void forgotPassword(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
        otpStorage.put(email, otp);

        System.out.println("OTP for " + email + ": " + otp);
    }

    /**
     * Verifies the OTP entered by the user against the stored OTP.
     * Returns true if the OTP matches, false otherwise.
     */
    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    /**
     * Resets the user's password if the OTP is valid.
     * The OTP is removed from storage after successful password reset.
     */
    public void resetPassword(String email, String otp, String newPassword) {
        if (!verifyOtp(email, otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        UserDto user = userService.findByEmail(email);

        user.setPassword(newPassword);
        userService.updateUser(user);

        otpStorage.remove(email); // Clean up OTP after use
    }
}
