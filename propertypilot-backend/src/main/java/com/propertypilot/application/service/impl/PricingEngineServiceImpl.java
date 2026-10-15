package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.PricingCalculatorService;
import com.propertypilot.application.service.PricingEngineService;
import com.propertypilot.application.service.PricingProfileLoaderService;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.application.service.SubscriptionPaymentVerificationService;
import com.propertypilot.domain.constants.LaunchPricingParameterCodes;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Transactional
public class PricingEngineServiceImpl implements PricingEngineService {
    private static final List<String> ACTIVE_REQUEST_STATES = List.of("NEW", "ASSIGNED", "ACCEPTED", "IN_PROGRESS");
    private static final List<String> COUNTED_REDEMPTION_STATES = List.of("RESERVED", "APPLIED");
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final double EARTH_RADIUS_KM = 6371.0088;

    private final CustomerJpaRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final ServiceRepository serviceRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final PricingCalculatorService calculator;
    private final PricingProfileLoaderService profileLoader;
    private final PropertyLocationRepository propertyLocationRepository;
    private final CoverageZoneRepository coverageZoneRepository;
    private final ClusterRuleRepository clusterRuleRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final CustomerSubscriptionRepository subscriptionRepository;
    private final PricingEstimateRepository estimateRepository;
    private final PricingQuoteLineItemRepository lineItemRepository;
    private final PricingDiscountApplicationRepository discountRepository;
    private final SecurityService securityService;
    private final SubscriptionPaymentVerificationService subscriptionPaymentVerificationService;
    private final EligibilityEngine eligibilityEngine;
    private final GoogleMapsDistanceService googleMapsDistanceService;

    public PricingEngineServiceImpl(CustomerJpaRepository customerRepository,
            PropertyRepository propertyRepository, ServiceRepository serviceRepository,
            PricingRuleRepository pricingRuleRepository, PricingCalculatorService calculator,
            PricingProfileLoaderService profileLoader, PropertyLocationRepository propertyLocationRepository,
            CoverageZoneRepository coverageZoneRepository, ClusterRuleRepository clusterRuleRepository,
            ServiceRequestRepository serviceRequestRepository, CouponRepository couponRepository,
            CouponRedemptionRepository couponRedemptionRepository,
            CustomerSubscriptionRepository subscriptionRepository, PricingEstimateRepository estimateRepository,
            PricingQuoteLineItemRepository lineItemRepository,
            PricingDiscountApplicationRepository discountRepository, SecurityService securityService,
            SubscriptionPaymentVerificationService subscriptionPaymentVerificationService,
            EligibilityEngine eligibilityEngine, GoogleMapsDistanceService googleMapsDistanceService) {
        this.customerRepository = customerRepository;
        this.propertyRepository = propertyRepository;
        this.serviceRepository = serviceRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.calculator = calculator;
        this.profileLoader = profileLoader;
        this.propertyLocationRepository = propertyLocationRepository;
        this.coverageZoneRepository = coverageZoneRepository;
        this.clusterRuleRepository = clusterRuleRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.estimateRepository = estimateRepository;
        this.lineItemRepository = lineItemRepository;
        this.discountRepository = discountRepository;
        this.securityService = securityService;
        this.subscriptionPaymentVerificationService = subscriptionPaymentVerificationService;
        this.eligibilityEngine = eligibilityEngine;
        this.googleMapsDistanceService = googleMapsDistanceService;
    }

    @Override
    @Transactional(readOnly = true)
    public PricingEstimateRequirementsResponse getCustomerEstimateRequirements(UUID propertyId, UUID serviceId) {
        CustomerEntity customer = securityService.getCurrentCustomer();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (property.getCustomer() == null || !customer.getCustomerId().equals(property.getCustomer().getCustomerId())) {
            throw new BusinessException("The selected property has no valid customer owner");
        }
        securityService.validatePropertyOwnership(property);
        if (property.getArchivedAt() != null || "ARCHIVED".equalsIgnoreCase(property.getStatus())) {
            throw new BusinessException("Archived properties cannot be used for a pricing estimate");
        }
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!Boolean.TRUE.equals(service.getActiveFlag()) || !Boolean.TRUE.equals(service.getBillable())) {
            throw new BusinessException("This service is no longer available for pricing");
        }
        eligibilityEngine.validateEligibility(serviceId, propertyId);
        PricingRuleEntity rule = activePricingRule(service, OffsetDateTime.now());
        ServicePricingRuleDefinition definition = ServicePricingRuleDefinition.parse(
                rule == null ? null : rule.getRuleDefinition());
        boolean percentage = ServicePricingRuleDefinition.PERCENT_OF_LOAN_AMOUNT.equals(definition.pricingModel());
        return new PricingEstimateRequirementsResponse(definition.pricingModel(), definition.amountBasis(),
                percentage && rule != null ? rule.getBaseAmount() : null,
                percentage ? definition.minimumFee() : null,
                percentage ? definition.maximumFee() : null);
    }

    @Override
    public PricingEstimateResponse calculateCustomerEstimate(CreatePricingEstimateRequest request) {
        CustomerEntity customer = securityService.getCurrentCustomer();
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (property.getCustomer() == null
                || !customer.getCustomerId().equals(property.getCustomer().getCustomerId())) {
            throw new BusinessException("The selected property has no valid customer owner");
        }
        securityService.validatePropertyOwnership(property);
        if (property.getArchivedAt() != null || "ARCHIVED".equalsIgnoreCase(property.getStatus())) {
            throw new BusinessException("Archived properties cannot be used for a pricing estimate");
        }
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!Boolean.TRUE.equals(service.getActiveFlag()) || !Boolean.TRUE.equals(service.getBillable())) {
            throw new BusinessException("This service is no longer available for pricing");
        }
        eligibilityEngine.validateEligibility(service.getServiceId(), property.getPropertyId());
        request.setCustomerId(customer.getCustomerId());
        boolean visitBased = Boolean.TRUE.equals(service.getRequiresVisit()) || Boolean.TRUE.equals(service.getVisitMandatory());
        if (visitBased) {
            PropertyLocationEntity location = propertyLocationRepository.findByProperty_PropertyId(property.getPropertyId())
                    .orElseThrow(() -> new BusinessException("A travel-based estimate is not available because the property location is missing. Operations will prepare the quote."));
            BigDecimal routeDistanceKm = googleMapsDistanceService.calculateOneWayRoadDistanceKm(location);
            // Google returns the authoritative road route; never accept client-supplied distance.
            request.setOneWayDistanceKm(routeDistanceKm.doubleValue());
        } else {
            // No-visit estimates intentionally have no travel component. Client distance is ignored.
            request.setOneWayDistanceKm(0d);
        }
        return calculateEstimateFor(customer, property, service, request, false);
    }

    @Override
    public PricingEstimateResponse calculateEstimate(CreatePricingEstimateRequest request) {
        securityService.validateOperationsAccess();
        if (request.getCustomerId() == null || request.getOneWayDistanceKm() == null) {
            throw new BusinessException("Customer and one-way distance are required for Operations pricing");
        }
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (property.getCustomer() == null || !customer.getCustomerId().equals(property.getCustomer().getCustomerId())) {
            throw new BusinessException("Property does not belong to the requested customer");
        }
        if (!Boolean.TRUE.equals(service.getActiveFlag()) || !Boolean.TRUE.equals(service.getBillable())) {
            throw new BusinessException("Only active, billable services can be priced");
        }

        return calculateEstimateFor(customer, property, service, request, true);
    }

    private PricingEstimateResponse calculateEstimateFor(CustomerEntity customer, Property property,
            ServiceEntity service, CreatePricingEstimateRequest request, boolean persistSubmittedDistance) {

        OffsetDateTime now = OffsetDateTime.now();
        PricingRuleEntity activeRule = activePricingRule(service, now);
        ServicePricingRuleDefinition priceDefinition = ServicePricingRuleDefinition.parse(
                activeRule == null ? null : activeRule.getRuleDefinition());
        BigDecimal ratePercent = ServicePricingRuleDefinition.PERCENT_OF_LOAN_AMOUNT.equals(priceDefinition.pricingModel())
                ? activeRule == null ? null : activeRule.getBaseAmount() : null;
        BigDecimal serverBaseCost;
        if (ratePercent != null) {
            BigDecimal loanAmount = request.getRequestedLoanAmount();
            if (loanAmount == null || loanAmount.signum() <= 0) {
                throw new BusinessException("Enter a requested loan amount to calculate this estimate");
            }
            serverBaseCost = loanAmount.multiply(ratePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            serverBaseCost = serverBaseCost.max(priceDefinition.minimumFee());
            if (priceDefinition.maximumFee() != null) serverBaseCost = serverBaseCost.min(priceDefinition.maximumFee());
        } else {
            serverBaseCost = activeRule == null ? service.getBasePrice() : activeRule.getBaseAmount();
        }
        String currency = activeRule == null || activeRule.getCurrencyCode() == null
                ? "INR" : activeRule.getCurrencyCode();
        if (serverBaseCost == null || serverBaseCost.signum() <= 0) {
            throw new BusinessException("A positive service price must be configured before estimating");
        }

        request.setVendorCost(serverBaseCost);
        PricingCalculationResponse calculation = calculator.calculate(request);
        PricingBreakdownResponse standard = calculation.getStandardBreakdown();
        PricingBreakdownResponse customerBreakdown = calculation.getCustomerBreakdown();
        if (customerBreakdown.getSubtotal().compareTo(standard.getSubtotal()) > 0) {
            throw new BusinessException("Customer pricing exceeds standard pricing; review profile configuration");
        }

        List<AppliedDiscount> applied = new ArrayList<>();
        BigDecimal remainingSubtotal = customerBreakdown.getSubtotal();
        Map<String, String> customerProfile = profileLoader.loadProfile("CUSTOMER");
        BigDecimal subscriptionPercent = getActiveSubscriptionDiscount(customer, service, now);
        DiscountResult subscriptionDiscount = percentageDiscount(remainingSubtotal, subscriptionPercent);
        remainingSubtotal = append(applied, "SUBSCRIPTION", "Subscription benefit", subscriptionDiscount,
                remainingSubtotal, null, Map.of("benefitKey", "serviceDiscountPercent"));

        DiscountResult clusterDiscount = getClusterDiscount(property, service, remainingSubtotal);
        remainingSubtotal = append(applied, "CLUSTER", "Cluster demand discount", clusterDiscount,
                remainingSubtotal, null, Map.of());

        // ETA discount configuration is optional. Do not parse it unless the customer
        // actually requested an ETA-based discount; invalid dormant settings must not
        // prevent a normal service estimate from being generated.
        DiscountResult etaDiscount = DiscountResult.none();
        if (request.getEtaDays() != null) {
            int etaMinimumDays = integerParameter(customerProfile, LaunchPricingParameterCodes.ETA_DISCOUNT_MIN_DAYS);
            if (request.getEtaDays() >= etaMinimumDays) {
                BigDecimal etaPercent = decimalParameter(customerProfile, LaunchPricingParameterCodes.ETA_DISCOUNT_PERCENT);
                etaDiscount = percentageDiscount(remainingSubtotal, etaPercent);
            }
        }
        remainingSubtotal = append(applied, "ETA", "Flexible ETA discount", etaDiscount,
                remainingSubtotal, null, Map.of("etaDays", request.getEtaDays() == null ? 0 : request.getEtaDays()));

        DiscountResult couponDiscount = calculateCouponDiscount(request.getCouponCode(), customer,
                customerBreakdown.getSubtotal(), remainingSubtotal, now);
        remainingSubtotal = append(applied, "COUPON", "Coupon discount", couponDiscount,
                remainingSubtotal, couponDiscount.couponId(), Map.of());

        BigDecimal discountTotal = customerBreakdown.getSubtotal().subtract(remainingSubtotal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal gstPercent = decimalParameter(customerProfile, LaunchPricingParameterCodes.GST_PERCENT);
        BigDecimal tax = remainingSubtotal.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        customerBreakdown.setClusterDiscount(amountFor(applied, "CLUSTER"));
        customerBreakdown.setEtaDiscount(amountFor(applied, "ETA"));
        customerBreakdown.setGstAmount(tax);
        customerBreakdown.setTotalAmount(remainingSubtotal.add(tax).setScale(2, RoundingMode.HALF_UP));

        PricingEstimateEntity estimate = new PricingEstimateEntity();
        estimate.setCustomer(customer);
        estimate.setProperty(property);
        estimate.setService(service);
        estimate.setSubtotal(customerBreakdown.getSubtotal().setScale(2, RoundingMode.HALF_UP));
        estimate.setDiscountAmount(discountTotal);
        estimate.setTaxAmount(tax);
        estimate.setTotalAmount(customerBreakdown.getTotalAmount());
        estimate.setCurrencyCode(currency);
        estimate.setStatus("ACTIVE");
        estimate.setExpiresAt(now.plusMinutes(30));
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("calculatedAt", now.toString());
        snapshot.put("pricingRuleId", activeRule == null ? null : activeRule.getPriceRuleId());
        // Google Maps route output is used only for this calculation and response; do not persist
        // the provider's distance content in the long-lived estimate snapshot.
        if (persistSubmittedDistance) snapshot.put("oneWayDistanceKm", request.getOneWayDistanceKm());
        snapshot.put("travelDistanceMode", customerProfile.get(LaunchPricingParameterCodes.TRAVEL_DISTANCE_MODE));
        snapshot.put("serverBaseCost", serverBaseCost);
        snapshot.put("pricingModel", priceDefinition.pricingModel());
        if (ratePercent != null) {
            snapshot.put("amountBasis", priceDefinition.amountBasis());
            snapshot.put("requestedLoanAmount", request.getRequestedLoanAmount());
            snapshot.put("ratePercent", ratePercent);
            snapshot.put("calculatedServiceFee", serverBaseCost);
        }
        snapshot.put("standardBreakdown", breakdownMap(standard));
        snapshot.put("customerBreakdown", breakdownMap(customerBreakdown));
        snapshot.put("discounts", applied.stream().map(AppliedDiscount::snapshot).toList());
        estimate.setCalculationSnapshot(snapshot);
        PricingEstimateEntity saved = estimateRepository.save(estimate);

        List<PricingEstimateLineResponse> lines = buildAndSaveLines(saved, service, customerBreakdown, applied);
        List<PricingDiscountResponse> discountResponses = new ArrayList<>();
        for (AppliedDiscount item : applied) {
            PricingDiscountApplicationEntity entity = new PricingDiscountApplicationEntity();
            entity.setPricingEstimate(saved);
            entity.setCouponId(item.couponId());
            entity.setDiscountType(switch (item.type()) {
                case "COUPON" -> "COUPON";
                case "SUBSCRIPTION" -> "PLAN";
                default -> "PROMOTION";
            });
            entity.setDescription(item.description());
            entity.setAmount(item.amount());
            entity.setMetadata(Map.of("sourceType", item.type()));
            discountRepository.save(entity);
            discountResponses.add(new PricingDiscountResponse(item.type(), item.description(), item.amount()));
        }

        PricingEstimateResponse response = new PricingEstimateResponse();
        response.setPricingEstimateId(saved.getPricingEstimateId());
        response.setCustomerId(customer.getCustomerId());
        response.setPropertyId(property.getPropertyId());
        response.setServiceId(service.getServiceId());
        response.setServiceName(service.getServiceName());
        response.setSubtotal(saved.getSubtotal());
        response.setDiscountAmount(saved.getDiscountAmount());
        response.setTaxAmount(saved.getTaxAmount());
        response.setTotalAmount(saved.getTotalAmount());
        response.setCurrencyCode(saved.getCurrencyCode());
        response.setStatus(saved.getStatus());
        response.setExpiresAt(saved.getExpiresAt());
        response.setStandardPrice(standard.getTotalAmount());
        response.setCustomerPrice(saved.getTotalAmount());
        response.setSavings(standard.getTotalAmount().subtract(saved.getTotalAmount()).max(ZERO));
        response.setStandardBreakdown(standard);
        response.setCustomerBreakdown(customerBreakdown);
        response.setLineItems(lines);
        response.setDiscounts(discountResponses);
        response.setPricingModel(priceDefinition.pricingModel());
        response.setAmountBasis(priceDefinition.amountBasis());
        response.setPricingInputAmount(ratePercent == null ? null : request.getRequestedLoanAmount());
        response.setRatePercent(ratePercent);
        response.setTravelDistanceKm(BigDecimal.valueOf(request.getOneWayDistanceKm()).setScale(3, RoundingMode.HALF_UP));
        response.setTravelDistanceMode(customerProfile.get(LaunchPricingParameterCodes.TRAVEL_DISTANCE_MODE));
        return response;
    }

    private PricingRuleEntity activePricingRule(ServiceEntity service, OffsetDateTime now) {
        return pricingRuleRepository.findByServiceServiceIdAndStatus(service.getServiceId(), "ACTIVE").stream()
                .filter(rule -> rule.getEffectiveFrom() != null && !rule.getEffectiveFrom().isAfter(now))
                .filter(rule -> rule.getEffectiveTo() == null || !rule.getEffectiveTo().isBefore(now))
                .max(Comparator.comparing(PricingRuleEntity::getEffectiveFrom)).orElse(null);
    }

    BigDecimal getActiveSubscriptionDiscount(CustomerEntity customer, ServiceEntity service, OffsetDateTime now) {
        LocalDate today = now.toLocalDate();
        return subscriptionRepository.findByCustomer_CustomerIdAndStatus(customer.getCustomerId(), "ACTIVE").stream()
                .filter(subscription -> subscription.getStartDate() != null && !subscription.getStartDate().isAfter(today))
                .filter(subscription -> subscription.getEndDate() == null || !subscription.getEndDate().isBefore(today))
                .filter(subscriptionPaymentVerificationService::isPaymentVerified)
                .map(CustomerSubscriptionEntity::getPlanVersion)
                .filter(Objects::nonNull)
                .map(SubscriptionPlanVersionEntity::getBenefitsJson)
                .filter(Objects::nonNull)
                .map(benefits -> benefits.get("serviceDiscountPercent"))
                .filter(Objects::nonNull)
                .map(value -> new BigDecimal(value.toString()))
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private DiscountResult getClusterDiscount(Property property, ServiceEntity service, BigDecimal remaining) {
        PropertyLocationEntity location = propertyLocationRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        if (location == null || location.getLatitude() == null || location.getLongitude() == null
                || location.getCoverageZoneId() == null) return DiscountResult.none();
        CoverageZoneEntity zone = coverageZoneRepository.findById(location.getCoverageZoneId()).orElse(null);
        if (zone == null || !"ACTIVE".equalsIgnoreCase(zone.getStatus()) || zone.getCluster() == null
                || !"ACTIVE".equalsIgnoreCase(zone.getCluster().getStatus())) return DiscountResult.none();
        List<ClusterRuleEntity> rules = clusterRuleRepository.findByStatusAndServiceTypeIn("ACTIVE",
                List.of(service.getCategory(), service.getServiceCode()));
        List<ServiceRequestEntity> requests = serviceRequestRepository.findByService_ServiceIdAndStatusIn(
                service.getServiceId(), ACTIVE_REQUEST_STATES);
        BigDecimal best = BigDecimal.ZERO;
        for (ClusterRuleEntity rule : rules) {
            if (rule.getRadiusKm() == null || rule.getRadiusKm().signum() <= 0
                    || rule.getDiscountPercentage() == null || rule.getMinimumRequests() == null) continue;
            long nearby = requests.stream().filter(request -> {
                Property requestProperty = request.getProperty();
                if (requestProperty == null) return false;
                PropertyLocationEntity requestLocation = propertyLocationRepository
                        .findByProperty_PropertyId(requestProperty.getPropertyId()).orElse(null);
                if (requestLocation == null || requestLocation.getLatitude() == null || requestLocation.getLongitude() == null
                        || requestLocation.getCoverageZoneId() == null) return false;
                CoverageZoneEntity requestZone = coverageZoneRepository.findById(requestLocation.getCoverageZoneId()).orElse(null);
                return requestZone != null && requestZone.getCluster() != null
                        && zone.getCluster().getClusterId().equals(requestZone.getCluster().getClusterId())
                        && distanceKm(location, requestLocation).compareTo(rule.getRadiusKm()) <= 0;
            }).count() + 1; // include the estimate being priced in the eligible group
            if (nearby >= rule.getMinimumRequests() && rule.getDiscountPercentage().compareTo(best) > 0) {
                best = rule.getDiscountPercentage();
            }
        }
        return percentageDiscount(remaining, best);
    }

    private DiscountResult calculateCouponDiscount(String code, CustomerEntity customer,
            BigDecimal originalSubtotal, BigDecimal remaining, OffsetDateTime now) {
        if (code == null || code.isBlank()) return DiscountResult.none();
        CouponEntity coupon = couponRepository.findByCouponCode(code.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new BusinessException("Coupon is invalid or unavailable"));
        if (!"ACTIVE".equalsIgnoreCase(coupon.getStatus()) || coupon.getValidFrom() == null
                || coupon.getValidTo() == null || now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())
                || originalSubtotal.compareTo(Optional.ofNullable(coupon.getMinimumOrderAmount()).orElse(BigDecimal.ZERO)) < 0) {
            throw new BusinessException("Coupon is not currently valid for this estimate");
        }
        long globalUses = couponRedemptionRepository.countByCoupon_CouponIdAndStatusIn(
                coupon.getCouponId(), COUNTED_REDEMPTION_STATES);
        long customerUses = couponRedemptionRepository.countByCoupon_CouponIdAndCustomer_CustomerIdAndStatusIn(
                coupon.getCouponId(), customer.getCustomerId(), COUNTED_REDEMPTION_STATES);
        if ((coupon.getRedemptionLimit() != null && globalUses >= coupon.getRedemptionLimit())
                || (coupon.getPerCustomerLimit() != null && customerUses >= coupon.getPerCustomerLimit())) {
            throw new BusinessException("Coupon redemption limit has been reached");
        }
        BigDecimal raw = "PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())
                ? originalSubtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue();
        if (coupon.getMaximumDiscount() != null) raw = raw.min(coupon.getMaximumDiscount());
        return new DiscountResult(raw.min(remaining).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP), coupon.getCouponId());
    }

    private BigDecimal append(List<AppliedDiscount> list, String type, String description,
            DiscountResult result, BigDecimal remaining, UUID couponId, Map<String, Object> metadata) {
        BigDecimal amount = result.amount().min(remaining).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        if (amount.signum() > 0) list.add(new AppliedDiscount(type, description, amount, couponId, metadata));
        return remaining.subtract(amount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private DiscountResult percentageDiscount(BigDecimal base, BigDecimal percent) {
        if (percent == null || percent.signum() <= 0) return DiscountResult.none();
        if (percent.compareTo(BigDecimal.valueOf(100)) > 0) throw new BusinessException("Discount percentage exceeds 100");
        return new DiscountResult(base.multiply(percent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP), null);
    }

    private BigDecimal decimalParameter(Map<String, String> profile, String code) {
        try { return new BigDecimal(profile.get(code)); }
        catch (RuntimeException ex) { throw new BusinessException("Invalid active pricing parameter: " + code); }
    }

    private int integerParameter(Map<String, String> profile, String code) {
        try { return Integer.parseInt(profile.get(code)); }
        catch (RuntimeException ex) { throw new BusinessException("Invalid active pricing parameter: " + code); }
    }

    private BigDecimal amountFor(List<AppliedDiscount> discounts, String type) {
        return discounts.stream().filter(item -> type.equals(item.type())).map(AppliedDiscount::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private List<PricingEstimateLineResponse> buildAndSaveLines(PricingEstimateEntity estimate, ServiceEntity service,
            PricingBreakdownResponse breakdown, List<AppliedDiscount> discounts) {
        List<PricingEstimateLineResponse> response = new ArrayList<>();
        addLine(estimate, service, response, "SERVICE", service.getServiceName(), breakdown.getVendorCost(), 1, 1);
        addLine(estimate, service, response, "FEE", "Travel", breakdown.getTravelCost(), 1, 2);
        addLine(estimate, service, response, "FEE", "Agent DA", breakdown.getAgentDa(), 1, 3);
        addLine(estimate, service, response, "FEE", "Operational overhead", breakdown.getOverheadAmount(), 1, 4);
        addLine(estimate, service, response, "FEE", "PropertyPilot margin", breakdown.getMarginAmount(), 1, 5);
        int order = 6;
        for (AppliedDiscount discount : discounts) {
            addLine(estimate, service, response, "DISCOUNT", discount.description(), discount.amount(), order, order);
            order++;
        }
        addLine(estimate, service, response, "TAX", "GST", breakdown.getGstAmount(), order, order);
        return response;
    }

    private void addLine(PricingEstimateEntity estimate, ServiceEntity service, List<PricingEstimateLineResponse> result,
            String type, String description, BigDecimal amount, int sort, int order) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO.setScale(2) : amount.setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO);
        PricingQuoteLineItemEntity line = new PricingQuoteLineItemEntity();
        line.setPricingEstimate(estimate);
        line.setService(service);
        line.setLineType(type);
        line.setDescription(description);
        line.setQuantity(BigDecimal.ONE);
        line.setUnitAmount(safeAmount);
        line.setLineAmount(safeAmount);
        line.setSortOrder(order);
        lineItemRepository.save(line);
        result.add(new PricingEstimateLineResponse(type, description, BigDecimal.ONE, safeAmount, safeAmount));
    }

    private Map<String, Object> breakdownMap(PricingBreakdownResponse value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("vendorCost", value.getVendorCost());
        map.put("travelCost", value.getTravelCost());
        map.put("agentDa", value.getAgentDa());
        map.put("overhead", value.getOverheadAmount());
        map.put("margin", value.getMarginAmount());
        map.put("subtotal", value.getSubtotal());
        map.put("gst", value.getGstAmount());
        map.put("total", value.getTotalAmount());
        return map;
    }

    private BigDecimal distanceKm(PropertyLocationEntity a, PropertyLocationEntity b) {
        double lat1 = Math.toRadians(a.getLatitude().doubleValue());
        double lat2 = Math.toRadians(b.getLatitude().doubleValue());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.getLongitude().doubleValue() - a.getLongitude().doubleValue());
        double h = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        return BigDecimal.valueOf(EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h)))
                .setScale(3, RoundingMode.HALF_UP);
    }

    private record DiscountResult(BigDecimal amount, UUID couponId) {
        static DiscountResult none() { return new DiscountResult(BigDecimal.ZERO, null); }
    }
    private record AppliedDiscount(String type, String description, BigDecimal amount, UUID couponId,
                                   Map<String, Object> metadata) {
        Map<String, Object> snapshot() {
            Map<String, Object> value = new LinkedHashMap<>(metadata);
            value.put("type", type);
            value.put("description", description);
            value.put("amount", amount);
            value.put("couponId", couponId);
            return value;
        }
    }
}
