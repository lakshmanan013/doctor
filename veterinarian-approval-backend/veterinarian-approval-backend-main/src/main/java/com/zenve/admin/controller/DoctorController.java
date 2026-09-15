package com.zenve.admin.controller;

import com.zenve.admin.dto.CreateDoctorRequest;
import com.zenve.admin.dto.DoctorDto;
import com.zenve.admin.dto.DoctorsResponse;
import com.zenve.admin.dto.RegisterDoctorRequest;
import com.zenve.admin.dto.RejectRequest;
import com.zenve.admin.dto.VerifyRequest;
import com.zenve.admin.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public DoctorsResponse list(@RequestParam(defaultValue = "all") String status) {
        return doctorService.list(status);
    }

    @PostMapping("/{id}/approve")
    public DoctorDto approve(@PathVariable String id) {
        return doctorService.approve(id);
    }

    @PostMapping("/{id}/reject")
    public DoctorDto reject(@PathVariable String id, @RequestBody(required = false) RejectRequest request) {
        String reason = request != null ? request.reason() : null;
        return doctorService.reject(id, reason);
    }

    /**
     * Marks one Verification panel item (Veterinary registration, KYC
     * verification, Digital signature, or State council sync) as verified.
     */
    @PostMapping("/{id}/verify")
    public DoctorDto verify(@PathVariable String id, @Valid @RequestBody VerifyRequest request) {
        return doctorService.verify(id, request.item());
    }

    /**
     * Admin-initiated doctor account creation. Unlike /register, this is
     * called directly from the admin frontend, not the doctor app.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto create(@Valid @RequestBody CreateDoctorRequest request) {
        return doctorService.create(request);
    }

    /**
     * Public endpoint used by the doctor-facing app to submit a new registration.
     * Not called by the admin frontend itself, but kept here so this backend is a
     * complete, runnable source of pending registrations for the admin CRM to review.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto register(@Valid @RequestBody RegisterDoctorRequest request) {
        return doctorService.register(request);
    }
}
