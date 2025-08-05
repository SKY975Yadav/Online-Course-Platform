package onlinecourseplatform.service;

import onlinecourseplatform.dto.requestDTOs.ChangePasswordRequest;
import onlinecourseplatform.dto.requestDTOs.UpdateUserRequest;
import onlinecourseplatform.dto.responseDTOs.UserResponseDTO;
import onlinecourseplatform.entity.User;
import onlinecourseplatform.repository.UserRepository;
import onlinecourseplatform.utility.Conversion;
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
    private final Conversion conversion;

    /**
     * Retrieves a user based on their email address.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Returns a list of all registered users.
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(conversion::toResponseDto).collect(Collectors.toList());
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
            case STUDENT ->
                log.info("Deleting Student User id={}", id);

            case INSTRUCTOR ->
                log.info("Deleting Instructor User  id={}", id);

            case ADMIN -> {
                log.warn("Cannot delete admin user with ID: {}", id);
                throw new RuntimeException("Cannot delete admin user");
            }
            default -> log.info("Deleting user with ID: {}", id);
        }
        userRepository.deleteById(id);

    }


    /**
     * Updates user details such as name and email, verifying the current password.
     */

    public UserResponseDTO updateUserDetails(UpdateUserRequest updateRequest, String email) {
        User user = findByEmail(email);

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
        User updatedUser = userRepository.save(user);
        return conversion.toResponseDto(updatedUser);
    }

    /**
     * Changes the password for a user.
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request, String email) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new SecurityException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Encode the password before saving
        userRepository.save(user);
    }


}
