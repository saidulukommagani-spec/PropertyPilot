package com.propertypilot.application.dto;

import jakarta.validation.constraints.Size;

public record RejectQuoteRequest(@Size(max = 1000) String reason) { }
