package com.zenve.admin.config;

import com.zenve.admin.model.Admin;
import com.zenve.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    public DataSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder, SeedProperties seedProperties) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedProperties = seedProperties;
    }

    @Override
    public void run(String... args) {
        String email = seedProperties.adminEmail();
        if (email != null && !adminRepository.existsByEmailIgnoreCase(email)) {
            Admin admin = Admin.builder()
                    .name(seedProperties.adminName() != null ? seedProperties.adminName() : "Zenve Admin")
                    .email(email)
                    .passwordHash(passwordEncoder.encode(seedProperties.adminPassword()))
                    .build();
            adminRepository.save(java.util.Objects.requireNonNull(admin));
            log.info("Seeded initial admin account: {}", email);
        }
    }
}
