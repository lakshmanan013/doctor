package com.zenve.admin.dto;

import com.zenve.admin.model.Doctor;

public record DoctorDto(
        String id,
        String fullName,
        String email,
        String phone,
        String clinicName,
        String qualification,
        String status,
        String rejectionReason,
        boolean veterinaryRegistrationVerified,
        boolean kycVerified,
        boolean digitalSignatureVerified,
        boolean stateCouncilSyncVerified,
        String createdAt,
        String updatedAt
) {
    public static DoctorDto from(Doctor d) {
        return new DoctorDto(
                d.getId(),
                d.getFullName(),
                d.getEmail(),
                d.getPhone(),
                d.getClinicName(),
                d.getQualification(),
                d.getStatus().name(),
                d.getRejectionReason(),
                d.isVeterinaryRegistrationVerified(),
                d.isKycVerified(),
                d.isDigitalSignatureVerified(),
                d.isStateCouncilSyncVerified(),
                d.getCreatedAt() != null ? d.getCreatedAt().toString() : null,
                d.getUpdatedAt() != null ? d.getUpdatedAt().toString() : null
        );
    }
}
