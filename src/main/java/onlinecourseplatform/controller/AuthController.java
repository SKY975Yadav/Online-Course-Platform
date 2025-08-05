package onlinecourseplatform.controller;

import onlinecourseplatform.dto.requestDTOs.UserRequestDTO;
import onlinecourseplatform.dto.requestDTOs.LoginRequest;
import onlinecourseplatform.dto.requestDTOs.ResetPasswordRequest;
import onlinecourseplatform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * Handles authentication-related endpoints such as login and registration.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token on successful login.
     */
    @Operation(summary = "Authenticate user and return JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    /**
     * Registers a new user as STUDENT or INSTRUCTOR. ADMIN role is not allowed.
     */
    @Operation(summary = "Register a new STUDENT or INSTRUCTOR")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO userDto) {
        return authService.register(userDto);
    }

    /**
     * Logs out the user by invalidating the JWT token.
     */
    @Operation(summary = "Logout the user")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        return authService.logout(principal.getName());
    }

    /**
     * If user forgot the password, allows user to change their password
     */
    @Operation(summary = "Request OTP for password reset")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "OTP is sent"));
    }

    /**
     * Verifies the OTP sent to the user's email for password reset.
     */
    @Operation(summary = "Verify OTP for password reset")
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = authService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "OTP verified"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid OTP"));
    }

    /**
     * Resets the user's password using the provided OTP and new password.
     */
    @Operation(summary = "Reset password using verified OTP")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

}
