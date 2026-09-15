package com.zenve.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDoctorRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        String clinicName,
        String qualification
) {
}
