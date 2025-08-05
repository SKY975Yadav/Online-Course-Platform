package onlinecourseplatform.config;

import onlinecourseplatform.entity.User;
import onlinecourseplatform.entity.Role;
import onlinecourseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .name("SAI KRISHNA")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .createdAt(LocalDateTime.now())
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created at startup.");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }
}