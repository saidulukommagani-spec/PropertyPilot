package com.propertypilot.application.service;

import com.propertypilot.application.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DealService {

    DealResponse createDeal(
            CreateDealRequest request);

    DealResponse getDeal(
            UUID dealId);

    List<DealResponse> getAllDeals();

    DealParticipantResponse addParticipant(
            UUID dealId,
            AddDealParticipantRequest request);

    List<DealParticipantResponse>
    getParticipants(
            UUID dealId);

    DealParticipantResponse selectBuyer(
            UUID dealId,
            UUID participantId);

            DealResponse updateDealStatus(
        UUID dealId,
        UpdateDealStatusRequest request);

        DealResponse finalizeDeal(
        UUID dealId,
        FinalizeDealRequest request);
DealResponse cancelDeal(
        UUID dealId,
        String reason);

        DealResponse completeDeal(
        UUID dealId,
        BigDecimal finalDealAmount);
}