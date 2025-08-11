package org.tishfy.springcatalog.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.model.Role;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.repository.RoleRepository;
import org.tishfy.springcatalog.repository.UserRepository;
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.enabled:true}")
    private boolean seedAdmin;

    @Value("${admin.seed.email:admin@example.com}")
    private String adminEmail;

    @Value("${admin.seed.password:admin123}")
    private String adminPassword;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("=== AdminDataInitializer starting ===");
        log.info("Seed admin enabled: {}", seedAdmin);
        log.info("Admin email: {}", adminEmail);

        if (!seedAdmin) {
            log.info("Admin seeding is disabled, skipping...");
            return;
        }

        try {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
                log.info("Creating ADMIN role...");
                Role r = new Role();
                r.setRoleName("ADMIN");
                Role saved = roleRepository.save(r);
                log.info("ADMIN role created with ID: {}", saved.getRoleId());
                return saved;
            });

            log.info("ADMIN role found/created: {}", adminRole.getRoleId());

            User existingUser = userRepository.findByEmailWithRole(adminEmail).orElse(null);
            if (existingUser != null) {
                log.info("Admin user already exists: {}", adminEmail);

                if (existingUser.getRole() == null || !adminRole.getRoleId().equals(existingUser.getRole().getRoleId())) {
                    log.info("Updating admin user role");
                    existingUser.setRole(adminRole);
                    existingUser = userRepository.save(existingUser);
                }

                log.info("Existing admin user role: {}",
                        existingUser.getRole() != null ? existingUser.getRole().getRoleName() : "NULL");
            } else {
                log.info("Creating new admin user: {}", adminEmail);
                User u = new User();
                u.setEmail(adminEmail);
                u.setName("Administrator");

                String hashedPassword = passwordEncoder.encode(adminPassword);
                u.setPasswordHash(hashedPassword);
                u.setRole(adminRole);
                u.setEnabled(true);

                User savedUser = userRepository.save(u);
                log.info("Admin user created successfully with ID: {}", savedUser.getUserId());
                log.info("Admin user enabled: {}", savedUser.isEnabled());
                log.info("Admin user role: {}", savedUser.getRole().getRoleName());
            }

            User verifyUser = userRepository.findByEmailWithRole(adminEmail).orElse(null);
            if (verifyUser != null) {
                log.info("=== VERIFICATION SUCCESSFUL ===");
                log.info("User ID: {}", verifyUser.getUserId());
                log.info("User Email: {}", verifyUser.getEmail());
                log.info("User Enabled: {}", verifyUser.isEnabled());

                if (verifyUser.getRole() != null) {
                    log.info("User Role: {}", verifyUser.getRole().getRoleName());
                } else {
                    log.warn("User Role is NULL!");
                }

                log.info("Password Hash Length: {}",
                        verifyUser.getPasswordHash() != null ? verifyUser.getPasswordHash().length() : 0);
            } else {
                log.error("VERIFICATION FAILED - User not found after creation!");
            }

        } catch (Exception e) {
            log.error("Error during admin initialization", e);
            throw new RuntimeException("Failed to initialize admin user", e);
        }

        log.info("=== AdminDataInitializer completed ===");
    }
}