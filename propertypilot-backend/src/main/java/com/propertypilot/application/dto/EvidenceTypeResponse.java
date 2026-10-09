package com.propertypilot.application.dto;

import java.util.UUID;

public record EvidenceTypeResponse(UUID evidenceTypeId, String code, String name, String category, String description) { }
