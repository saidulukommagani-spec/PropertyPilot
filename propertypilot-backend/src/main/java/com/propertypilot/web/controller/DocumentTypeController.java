package com.propertypilot.web.controller;

import com.propertypilot.application.dto.DocumentTypeResponse;
import com.propertypilot.application.service.DocumentTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document-types")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Document Type",
        description = "Document Type APIs")
public class DocumentTypeController {

    private final DocumentTypeService
            documentTypeService;

    @GetMapping(
            "/property-category/{propertyCategory}")
    @Operation(
            summary =
                    "Get document types by property category")
    public List<DocumentTypeResponse>
    getByPropertyCategory(

            @PathVariable
            @NotBlank
            String propertyCategory) {

        return documentTypeService
                .getByPropertyCategory(
                        propertyCategory);
    }
}