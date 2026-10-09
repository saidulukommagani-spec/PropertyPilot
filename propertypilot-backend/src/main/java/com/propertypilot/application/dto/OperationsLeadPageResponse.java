package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

/** Stable pagination contract for the Admin/Operations Lead queue. */
@Schema(description = "A page of searchable leads for authorized Operations staff.")
public record OperationsLeadPageResponse(
        @Schema(description = "Rows in this page") List<OperationsLeadResponse> content,
        @Schema(example = "0") int page,
        @Schema(example = "25") int size,
        @Schema(example = "120") long totalElements,
        @Schema(example = "5") int totalPages,
        boolean first,
        boolean last) {

    public static OperationsLeadPageResponse from(Page<OperationsLeadResponse> page) {
        return new OperationsLeadPageResponse(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
