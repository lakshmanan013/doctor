package com.zenve.admin.dto;

import jakarta.validation.constraints.NotNull;

public record VerifyRequest(
        @NotNull com.zenve.admin.model.VerificationItem item
) {
}
