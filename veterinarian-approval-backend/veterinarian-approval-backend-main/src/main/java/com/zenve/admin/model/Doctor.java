package com.zenve.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "clinic_name")
    private String clinicName;

    private String qualification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DoctorStatus status = DoctorStatus.pending;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "veterinary_registration_verified", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean veterinaryRegistrationVerified = false;

    @Column(name = "kyc_verified", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean kycVerified = false;

    @Column(name = "digital_signature_verified", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean digitalSignatureVerified = false;

    @Column(name = "state_council_sync_verified", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean stateCouncilSyncVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
