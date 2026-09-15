package com.propertypilot.application.service;

import com.propertypilot.application.dto.DocumentTypeResponse;

import java.util.List;

public interface DocumentTypeService {

    List<DocumentTypeResponse> getByPropertyCategory(
            String propertyCategory);
}