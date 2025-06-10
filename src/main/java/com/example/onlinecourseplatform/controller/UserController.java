package com.example.onlinecourseplatform.controller;

import com.example.onlinecourseplatform.dto.ChangePasswordRequest;
import com.example.onlinecourseplatform.dto.UpdateUserRequest;
import com.example.onlinecourseplatform.dto.UserDto;
import com.example.onlinecourseplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller for user-related actions like fetching, updating, deleting, etc.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get all users (Admin only).
     */
    @Operation(summary = "Get all users (Admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Admin requested all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Delete a user by ID (Admin only).
     */
    @Operation(summary = "Delete a user by ID (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Admin attempting to delete user with ID {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");

    }

    /**
     * Get current logged-in user.
     */

    @Operation(summary = "Get current logged-in user")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        UserDto user = userService.findByEmail(principal.getName());
        log.info("Fetching current user info: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user's name or email.
     */
    @Operation(summary = "Update current user's name or email")
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest updateRequest, Principal principal) {
        UserDto updatedUser = userService.updateUserDetails(updateRequest, principal.getName());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Change current user's password.
     */
    @Operation(summary = "Change current user's password")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, Principal principal) {

        userService.changePassword(changePasswordRequest, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }


}
