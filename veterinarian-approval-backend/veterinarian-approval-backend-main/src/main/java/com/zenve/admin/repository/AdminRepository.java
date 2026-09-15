package com.zenve.admin.repository;

import com.zenve.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
