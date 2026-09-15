package com.zenve.admin.service;

import com.zenve.admin.dto.AdminDto;
import com.zenve.admin.dto.LoginResponse;
import com.zenve.admin.exception.ApiException;
import com.zenve.admin.model.Admin;
import com.zenve.admin.repository.AdminRepository;
import com.zenve.admin.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String email, String password) {
        Admin admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(admin.getId(), admin.getEmail());
        return new LoginResponse(token, AdminDto.from(admin));
    }
}
