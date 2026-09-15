package com.zenve.admin.controller;

import com.zenve.admin.dto.AdminDto;
import com.zenve.admin.dto.LoginRequest;
import com.zenve.admin.dto.LoginResponse;
import com.zenve.admin.dto.MeResponse;
import com.zenve.admin.model.Admin;
import com.zenve.admin.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Admin admin) {
        return new MeResponse(AdminDto.from(admin));
    }
}
