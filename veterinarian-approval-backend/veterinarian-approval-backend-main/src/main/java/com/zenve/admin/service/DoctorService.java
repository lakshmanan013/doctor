package com.zenve.admin.service;

import com.zenve.admin.dto.*;
import com.zenve.admin.exception.ApiException;
import com.zenve.admin.model.Doctor;
import com.zenve.admin.model.DoctorStatus;
import com.zenve.admin.model.NotificationType;
import com.zenve.admin.model.VerificationItem;
import com.zenve.admin.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final NotificationService notificationService;
    private final DoctorAppNotifier doctorAppNotifier;

    public DoctorService(DoctorRepository doctorRepository, NotificationService notificationService,
                          DoctorAppNotifier doctorAppNotifier) {
        this.doctorRepository = doctorRepository;
        this.notificationService = notificationService;
        this.doctorAppNotifier = doctorAppNotifier;
    }

    public DoctorsResponse list(String statusFilter) {
        List<Doctor> doctors;
        String normalized = statusFilter == null ? "all" : statusFilter.toLowerCase();

        if ("all".equals(normalized)) {
            doctors = doctorRepository.findAllByOrderByCreatedAtDesc();
        } else {
            DoctorStatus status = parseStatus(normalized);
            doctors = doctorRepository.findByStatusOrderByCreatedAtDesc(status);
        }

        List<DoctorDto> dtos = doctors.stream().map(DoctorDto::from).toList();
        return new DoctorsResponse(dtos, counts());
    }

    private CountsDto counts() {
        long pending = doctorRepository.countByStatus(DoctorStatus.pending);
        long approved = doctorRepository.countByStatus(DoctorStatus.approved);
        long rejected = doctorRepository.countByStatus(DoctorStatus.rejected);
        long all = doctorRepository.count();
        return new CountsDto(pending, approved, rejected, all);
    }

    private DoctorStatus parseStatus(String raw) {
        try {
            return DoctorStatus.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Unknown status filter: " + raw);
        }
    }

    @Transactional
    public DoctorDto approve(String id) {
        Doctor doctor = getOrThrow(id);
        doctor.setStatus(DoctorStatus.approved);
        doctor.setRejectionReason(null);
        doctorRepository.save(doctor);

        notificationService.notify(
                NotificationType.doctor_approved,
                "Doctor approved",
                doctor.getFullName() + " was approved and can now access the platform.",
                doctor.getId()
        );

        doctorAppNotifier.notifyApproved(doctor);

        return DoctorDto.from(doctor);
    }

    @Transactional
    public DoctorDto reject(String id, String reason) {
        Doctor doctor = getOrThrow(id);
        doctor.setStatus(DoctorStatus.rejected);
        doctor.setRejectionReason((reason == null || reason.isBlank()) ? null : reason.trim());
        doctorRepository.save(doctor);

        String message = doctor.getFullName() + "'s registration was rejected"
                + (doctor.getRejectionReason() != null ? " — " + doctor.getRejectionReason() : ".");

        notificationService.notify(
                NotificationType.doctor_rejected,
                "Doctor rejected",
                message,
                doctor.getId()
        );

        doctorAppNotifier.notifyRejected(doctor);

        return DoctorDto.from(doctor);
    }

    /**
     * Marks one Verification panel item as verified for this doctor and
     * pushes the update to the doctor-facing backend so it shows up there.
     */
    @Transactional
    public DoctorDto verify(String id, VerificationItem item) {
        Doctor doctor = getOrThrow(id);

        switch (item) {
            case VETERINARY_REGISTRATION -> doctor.setVeterinaryRegistrationVerified(true);
            case KYC -> doctor.setKycVerified(true);
            case DIGITAL_SIGNATURE -> doctor.setDigitalSignatureVerified(true);
            case STATE_COUNCIL_SYNC -> doctor.setStateCouncilSyncVerified(true);
        }

        doctorRepository.save(doctor);
        doctorAppNotifier.notifyVerified(doctor, item);

        return DoctorDto.from(doctor);
    }

    /**
     * Admin-initiated doctor creation: skips the pending-approval step
     * entirely — the record is approved here and the login account is
     * created on the doctor-facing backend right away.
     */
    @Transactional
    public DoctorDto create(CreateDoctorRequest request) {
        Doctor doctor = Doctor.builder()
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone())
                .clinicName(request.clinicName())
                .qualification(request.qualification())
                .status(DoctorStatus.approved)
                .build();
        doctorRepository.save(doctor);

        notificationService.notify(
                NotificationType.doctor_approved,
                "Doctor account created",
                doctor.getFullName() + "'s account was created by an admin and can log in now.",
                doctor.getId()
        );

        doctorAppNotifier.notifyAccountCreated(doctor, request.password());

        return DoctorDto.from(doctor);
    }

    @Transactional
    public DoctorDto register(RegisterDoctorRequest request) {
        Doctor doctor = Doctor.builder()
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone())
                .clinicName(request.clinicName())
                .qualification(request.qualification())
                .status(DoctorStatus.pending)
                .build();
        doctorRepository.save(doctor);

        notificationService.notify(
                NotificationType.doctor_registered,
                "New doctor registration",
                doctor.getFullName() + " signed up and is waiting for approval.",
                doctor.getId()
        );

        return DoctorDto.from(doctor);
    }

    private Doctor getOrThrow(String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Doctor not found"));
    }
}
