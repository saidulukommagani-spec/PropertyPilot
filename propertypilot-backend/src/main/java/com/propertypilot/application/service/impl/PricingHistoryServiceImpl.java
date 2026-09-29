package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.PricingHistoryService;
import com.propertypilot.infrastructure.persistence.entity.PricingParameterHistoryEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingParameterHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PricingHistoryServiceImpl
        implements PricingHistoryService {

    private final PricingParameterHistoryRepository
            historyRepository;

    public PricingHistoryServiceImpl(
            PricingParameterHistoryRepository historyRepository) {

        this.historyRepository =
                historyRepository;
    }

    @Override
    public void recordChange(
            PricingProfileParameterEntity parameter,
            String oldValue,
            String newValue,
            UUID changedBy,
            String remarks) {

        PricingParameterHistoryEntity history =
                new PricingParameterHistoryEntity();

     history.setPricingParameter(
        parameter);

history.setParameterCode(
        parameter.getParameterCode());

        history.setOldValue(
                oldValue);

        history.setNewValue(
                newValue);

        history.setChangedBy(
                changedBy);

        history.setChangedAt(
                OffsetDateTime.now());

        history.setRemarks(
                remarks);

        historyRepository.save(
                history);
    }
    
}