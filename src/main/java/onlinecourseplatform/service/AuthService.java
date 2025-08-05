package onlinecourseplatform.service;

import onlinecourseplatform.dto.requestDTOs.UserRequestDTO;
import onlinecourseplatform.entity.Role;
import onlinecourseplatform.entity.User;
import onlinecourseplatform.repository.UserRepository;
import onlinecourseplatform.security.JwtUtil;
import onlinecourseplatform.utility.Conversion;
import onlinecourseplatform.utility.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final Utility utility;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final Conversion conversion;

    // Temporary in-memory OTP storage
    @Getter
    private final Map<String, String> otpStorage = new HashMap<>();

    /**
     * Authenticates a user and generates a JWT token if successful.
     * Returns user details along with the token.
     */
    public ResponseEntity<?> login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, password
                    )
            );

            User user = utility.findUserByEmail(email);
            String message,token;
            String existingToken = redisService.getToken(user.getId());
            if (existingToken != null) {
                token = redisService.getToken(user.getId());
                message = "User already logged in";
            } else {
                token = jwtUtil.generateToken(email);
                redisService.saveToken(user.getId(), token);
                log.info("Login successful for user: {}", email);
                message = "Login successful";
            }

            return ResponseEntity.ok(Map.of(
                    "token", token,
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
    public ResponseEntity<?> register(UserRequestDTO userDto) {
        if (utility.existsUserByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userDto.getRole() == Role.ADMIN) {
            log.warn("Attempt to register with ADMIN role: {}", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Admin role assignment not allowed"));
        }

        User user = conversion.toEntityFromRequest(userDto);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "id", savedUser.getId(),
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
        User user = utility.findUserByEmail(email);
        redisService.deleteToken(user.getId());
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

        //Update password
        User user = utility.findUserByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email); // Clean up OTP after use
        log.info("Password reset successfully for user: {}", email);
    }
}