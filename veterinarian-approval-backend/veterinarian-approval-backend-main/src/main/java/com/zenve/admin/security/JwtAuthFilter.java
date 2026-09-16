package com.zenve.admin.security;

import com.zenve.admin.model.Admin;
import com.zenve.admin.repository.AdminRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Reads the "Authorization: Bearer <token>" header, validates it, loads the
 * corresponding admin, and sets it as the request's authenticated principal.
 * Anonymous requests (no/invalid header) simply pass through unauthenticated;
 * Spring Security's authorization rules decide whether that's allowed.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    public JwtAuthFilter(JwtService jwtService, AdminRepository adminRepository) {
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Optional<Claims> claimsOpt = jwtService.parseClaims(token);

            if (claimsOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                String adminId = claimsOpt.get().getSubject();
                if (adminId != null) {
                    Optional<Admin> adminOpt = adminRepository.findById(adminId);

                    if (adminOpt.isPresent()) {
                        Admin admin = adminOpt.get();
                        var authToken = new UsernamePasswordAuthenticationToken(
                                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                        authToken.setDetails(admin);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
