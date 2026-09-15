package com.zenve.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Used by the admin CRM to create a doctor account directly, instead of
 * waiting for the doctor to self-register. The account is approved
 * immediately and can log in with the given password right away.
 */
public record CreateDoctorRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        String clinicName,
        String qualification,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password
) {
}
