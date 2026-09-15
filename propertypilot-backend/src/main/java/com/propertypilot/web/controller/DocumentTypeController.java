package com.propertypilot.web.controller;

import com.propertypilot.application.dto.DocumentTypeResponse;
import com.propertypilot.application.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService
            documentTypeService;

    @GetMapping("/property-category/{propertyCategory}")
    public List<DocumentTypeResponse>
    getByPropertyCategory(
            @PathVariable String propertyCategory) {

        return documentTypeService
                .getByPropertyCategory(
                        propertyCategory);
    }
}
