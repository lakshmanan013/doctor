package com.zenve.admin.repository;

import com.zenve.admin.model.Doctor;
import com.zenve.admin.model.DoctorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    List<Doctor> findAllByOrderByCreatedAtDesc();
    List<Doctor> findByStatusOrderByCreatedAtDesc(DoctorStatus status);
    long countByStatus(DoctorStatus status);
}
