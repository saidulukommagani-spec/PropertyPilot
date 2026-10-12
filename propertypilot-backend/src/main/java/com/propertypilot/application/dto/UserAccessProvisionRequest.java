package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAccessProvisionRequest(
        @NotBlank
        @Pattern(regexp = "AGENT|OPERATIONS|SUPPORT", message = "Access must be AGENT, OPERATIONS, or SUPPORT")
        String roleCode,
        boolean retainCustomerAccess,
        @Size(max = 120, message = "Region must not exceed 120 characters")
        String region,
        @NotBlank(message = "A reason for this access change is required")
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason
) {
}
