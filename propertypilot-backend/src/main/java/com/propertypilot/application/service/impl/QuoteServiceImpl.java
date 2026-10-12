package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.QuoteLineItemResponse;
import com.propertypilot.application.dto.QuoteResponse;
import com.propertypilot.application.service.QuoteService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.infrastructure.persistence.entity.CouponEntity;
import com.propertypilot.infrastructure.persistence.entity.CouponRedemptionEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingDiscountApplicationEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.repository.CouponRedemptionRepository;
import com.propertypilot.infrastructure.persistence.repository.CouponRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingDiscountApplicationRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingEstimateRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingQuoteLineItemRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuoteServiceImpl implements QuoteService {
    private static final List<String> COUNTED_REDEMPTION_STATES = List.of("RESERVED", "APPLIED");

    private final PricingEstimateRepository estimateRepository;
    private final PricingQuoteLineItemRepository lineItemRepository;
    private final PricingDiscountApplicationRepository discountRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final SecurityService securityService;
    private final ServiceRequestService serviceRequestService;

    public QuoteServiceImpl(PricingEstimateRepository estimateRepository,
            PricingQuoteLineItemRepository lineItemRepository,
            PricingDiscountApplicationRepository discountRepository,
            CouponRepository couponRepository,
            CouponRedemptionRepository redemptionRepository,
            SecurityService securityService,
            ServiceRequestService serviceRequestService) {
        this.estimateRepository = estimateRepository;
        this.lineItemRepository = lineItemRepository;
        this.discountRepository = discountRepository;
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
        this.securityService = securityService;
        this.serviceRequestService = serviceRequestService;
    }

    @Override
    @Transactional
    public List<QuoteResponse> getMyQuotes() {
        UUID customerId = securityService.getCurrentCustomerId();
        OffsetDateTime now = OffsetDateTime.now();
        List<PricingEstimateEntity> estimates = estimateRepository
                .findByCustomer_CustomerIdOrderByCreatedAtDesc(customerId);
        estimates.stream().filter(estimate -> expireIfNeeded(estimate, now)).forEach(estimateRepository::save);
        return estimates.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResponse> getAllQuotesForOperations() {
        securityService.validateOperationsAccess();
        return estimateRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(PricingEstimateEntity::getCreatedAt).reversed())
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public QuoteResponse getQuote(UUID quoteId) {
        PricingEstimateEntity estimate = findAuthorizedQuote(quoteId);
        if (expireIfNeeded(estimate, OffsetDateTime.now())) {
            estimate = estimateRepository.save(estimate);
        }
        return toResponse(estimate);
    }

    @Override
    @Transactional
    public QuoteResponse acceptQuote(UUID quoteId) {
        PricingEstimateEntity estimate = findAuthorizedQuoteForUpdate(quoteId);
        OffsetDateTime now = OffsetDateTime.now();
        if (expireIfNeeded(estimate, now)) {
            return toResponse(estimateRepository.save(estimate));
        }
        requireOpen(estimate);
        reserveCouponDiscounts(estimate, now);
        estimate.setStatus("ACCEPTED");
        estimate.setAcceptedAt(now);
        estimate.setAcceptedByUserId(securityService.getCurrentUserId());
        return toResponse(estimateRepository.save(estimate));
    }

    @Override
    @Transactional
    public ServiceRequestResponse acceptAndCreateRequest(UUID quoteId, CreateServiceRequestRequest request) {
        if (request == null) {
            throw new BusinessException("Service request details are required");
        }
        PricingEstimateEntity estimate = findAuthorizedQuoteForUpdate(quoteId);
        OffsetDateTime now = OffsetDateTime.now();
        if (expireIfNeeded(estimate, now)) {
            estimateRepository.save(estimate);
            throw new BusinessException("This quote has expired. Request a new estimate before booking");
        }
        if ("ACCEPTED".equals(estimate.getStatus())) {
            request.setQuoteId(quoteId);
            request.setPropertyId(estimate.getProperty().getPropertyId());
            request.setServiceId(estimate.getService().getServiceId());
            request.setServiceType(estimate.getService().getCategory());
            return serviceRequestService.createRequest(request);
        }
        requireOpen(estimate);
        reserveCouponDiscounts(estimate, now);
        estimate.setStatus("ACCEPTED");
        estimate.setAcceptedAt(now);
        estimate.setAcceptedByUserId(securityService.getCurrentUserId());
        estimateRepository.save(estimate);

        request.setQuoteId(quoteId);
        request.setPropertyId(estimate.getProperty().getPropertyId());
        request.setServiceId(estimate.getService().getServiceId());
        request.setServiceType(estimate.getService().getCategory());
        return serviceRequestService.createRequest(request);
    }

    @Override
    @Transactional
    public QuoteResponse rejectQuote(UUID quoteId, String reason) {
        if (reason != null && reason.length() > 1000) {
            throw new BusinessException("Quote rejection reason cannot exceed 1000 characters");
        }
        PricingEstimateEntity estimate = findAuthorizedQuoteForUpdate(quoteId);
        OffsetDateTime now = OffsetDateTime.now();
        if (expireIfNeeded(estimate, now)) {
            return toResponse(estimateRepository.save(estimate));
        }
        requireOpen(estimate);
        estimate.setStatus("REJECTED");
        estimate.setRejectedAt(now);
        estimate.setRejectedByUserId(securityService.getCurrentUserId());
        estimate.setRejectionReason(reason == null || reason.isBlank() ? null : reason.trim());
        return toResponse(estimateRepository.save(estimate));
    }

    @Override
    @Transactional
    public int expireQuotes() {
        List<PricingEstimateEntity> expired = estimateRepository
                .findByStatusAndExpiresAtLessThanEqual("ACTIVE", OffsetDateTime.now());
        expired.forEach(estimate -> estimate.setStatus("EXPIRED"));
        estimateRepository.saveAll(expired);
        return expired.size();
    }

    private PricingEstimateEntity findAuthorizedQuote(UUID quoteId) {
        PricingEstimateEntity estimate = estimateRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));
        securityService.validateCustomerAccess(estimate.getCustomer());
        return estimate;
    }

    private PricingEstimateEntity findAuthorizedQuoteForUpdate(UUID quoteId) {
        PricingEstimateEntity estimate = estimateRepository.findByIdForUpdate(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));
        securityService.validateCustomerAccess(estimate.getCustomer());
        return estimate;
    }

    private boolean expireIfNeeded(PricingEstimateEntity estimate, OffsetDateTime now) {
        if ("ACTIVE".equals(estimate.getStatus()) && !estimate.getExpiresAt().isAfter(now)) {
            estimate.setStatus("EXPIRED");
            return true;
        }
        return false;
    }

    private void requireOpen(PricingEstimateEntity estimate) {
        if (!"ACTIVE".equals(estimate.getStatus())) {
            throw new BusinessException("Quote is not open for a decision: " + estimate.getStatus());
        }
    }

    private void reserveCouponDiscounts(PricingEstimateEntity estimate, OffsetDateTime now) {
        List<PricingDiscountApplicationEntity> couponDiscounts = discountRepository
                .findByPricingEstimate_PricingEstimateId(estimate.getPricingEstimateId()).stream()
                .filter(discount -> discount.getCouponId() != null)
                .toList();
        for (PricingDiscountApplicationEntity discount : couponDiscounts) {
            if (redemptionRepository.existsByCoupon_CouponIdAndPricingEstimate_PricingEstimateIdAndStatusIn(
                    discount.getCouponId(), estimate.getPricingEstimateId(), COUNTED_REDEMPTION_STATES)) {
                continue;
            }
            CouponEntity coupon = couponRepository.findByIdForUpdate(discount.getCouponId())
                    .orElseThrow(() -> new BusinessException("The coupon attached to this quote is unavailable"));
            long globalUses = redemptionRepository.countByCoupon_CouponIdAndStatusIn(
                    coupon.getCouponId(), COUNTED_REDEMPTION_STATES);
            long customerUses = redemptionRepository.countByCoupon_CouponIdAndCustomer_CustomerIdAndStatusIn(
                    coupon.getCouponId(), estimate.getCustomer().getCustomerId(), COUNTED_REDEMPTION_STATES);
            if ((coupon.getRedemptionLimit() != null && globalUses >= coupon.getRedemptionLimit())
                    || (coupon.getPerCustomerLimit() != null && customerUses >= coupon.getPerCustomerLimit())) {
                throw new BusinessException("The coupon limit was reached before this quote was accepted");
            }
            CouponRedemptionEntity redemption = new CouponRedemptionEntity();
            redemption.setCoupon(coupon);
            redemption.setCustomer(estimate.getCustomer());
            redemption.setPricingEstimate(estimate);
            redemption.setRedeemedAt(now);
            redemption.setDiscountAmount(discount.getAmount());
            redemption.setStatus("RESERVED");
            redemptionRepository.save(redemption);
        }
    }

    private QuoteResponse toResponse(PricingEstimateEntity estimate) {
        List<com.propertypilot.infrastructure.persistence.entity.PricingQuoteLineItemEntity> savedLines =
                lineItemRepository.findByPricingEstimate_PricingEstimateIdOrderBySortOrderAsc(
                        estimate.getPricingEstimateId());
        String serviceName = savedLines.stream()
                .filter(line -> "SERVICE".equals(line.getLineType()))
                .map(com.propertypilot.infrastructure.persistence.entity.PricingQuoteLineItemEntity::getDescription)
                .findFirst().orElse(estimate.getService().getServiceName());
        List<QuoteLineItemResponse> lines = customerLineItems(estimate, savedLines, serviceName);
        BigDecimal standardPrice = standardPrice(estimate.getCalculationSnapshot());
        BigDecimal savings = standardPrice == null ? null : standardPrice.subtract(estimate.getTotalAmount()).max(BigDecimal.ZERO);
        String customerName = estimate.getCustomer() == null || estimate.getCustomer().getUser() == null
                ? null : estimate.getCustomer().getUser().getFullName();
        String customerEmail = estimate.getCustomer() == null || estimate.getCustomer().getUser() == null
                ? null : estimate.getCustomer().getUser().getEmail();
        String propertyName = estimate.getProperty() == null ? null : estimate.getProperty().getTitle();
        return new QuoteResponse(
                estimate.getPricingEstimateId(),
                estimate.getCustomer().getCustomerId(),
                estimate.getProperty() == null ? null : estimate.getProperty().getPropertyId(),
                estimate.getService().getServiceId(),
                serviceName,
                estimate.getStatus(),
                estimate.getSubtotal(),
                estimate.getDiscountAmount(),
                estimate.getTaxAmount(),
                estimate.getTotalAmount(),
                estimate.getCurrencyCode(),
                standardPrice,
                savings,
                estimate.getExpiresAt(),
                estimate.getAcceptedAt(),
                estimate.getRejectedAt(),
                estimate.getRejectionReason(),
                List.copyOf(lines),
                customerName,
                customerEmail,
                propertyName,
                estimate.getCreatedAt());
    }

    private List<QuoteLineItemResponse> customerLineItems(PricingEstimateEntity estimate,
            List<com.propertypilot.infrastructure.persistence.entity.PricingQuoteLineItemEntity> savedLines,
            String serviceName) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        for (var line : savedLines) {
            switch (line.getLineType()) {
                case "SERVICE", "FEE" -> subtotal = subtotal.add(line.getLineAmount());
                case "DISCOUNT" -> discount = discount.add(line.getLineAmount());
                case "TAX" -> tax = tax.add(line.getLineAmount());
                default -> { /* Internal quote components remain hidden from customers. */ }
            }
        }
        if (savedLines.isEmpty()) {
            subtotal = estimate.getSubtotal();
            discount = estimate.getDiscountAmount() == null ? BigDecimal.ZERO : estimate.getDiscountAmount();
            tax = estimate.getTaxAmount() == null ? BigDecimal.ZERO : estimate.getTaxAmount();
        }
        List<QuoteLineItemResponse> lines = new ArrayList<>();
        lines.add(new QuoteLineItemResponse("SERVICE", serviceName, subtotal));
        if (discount.signum() > 0) lines.add(new QuoteLineItemResponse("DISCOUNT", "Applied discounts", discount));
        if (tax.signum() > 0) lines.add(new QuoteLineItemResponse("TAX", "GST", tax));
        return lines;
    }

    private BigDecimal standardPrice(Map<String, Object> snapshot) {
        if (snapshot == null || !(snapshot.get("standardBreakdown") instanceof Map<?, ?> breakdown)) return null;
        Object total = breakdown.get("total");
        if (total instanceof BigDecimal amount) return amount;
        if (total instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        if (total instanceof String value) {
            try { return new BigDecimal(value); } catch (NumberFormatException ignored) { return null; }
        }
        return null;
    }
}
