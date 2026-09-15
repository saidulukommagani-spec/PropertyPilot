package com.propertypilot.application.service.impl;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;
import com.propertypilot.application.service.GpsVerificationService;
import com.propertypilot.infrastructure.persistence.entity.GpsVerificationEntity;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.GpsVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyLocationRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class GpsVerificationServiceImpl
        implements GpsVerificationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private static final BigDecimal
            GPS_THRESHOLD_KM =
            new BigDecimal("0.200");

    private final GpsVerificationRepository gpsRepository;

    private final VisitRepository visitRepository;

    private final PropertyLocationRepository
            propertyLocationRepository;

    public GpsVerificationServiceImpl(
            GpsVerificationRepository gpsRepository,
            VisitRepository visitRepository,
            PropertyLocationRepository propertyLocationRepository) {

        this.gpsRepository = gpsRepository;
        this.visitRepository = visitRepository;
        this.propertyLocationRepository =
                propertyLocationRepository;
    }
@Override
@Transactional
    public GpsVerificationResponse createVerification(
            CreateGpsVerificationRequest request) {

        VisitEntity visit =
                visitRepository.findById(
                                request.getVisitId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));

        ServiceRequestEntity serviceRequest =
                visit.getServiceRequest();

        PropertyLocationEntity propertyLocation =
                propertyLocationRepository
                        .findByProperty_PropertyId(
                                serviceRequest.getProperty()
                                        .getPropertyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property location not found"));

        BigDecimal distance =
                calculateDistance(
                        propertyLocation,
                        request.getLatitude(),
                        request.getLongitude());

        String status =
                distance.compareTo(
                        GPS_THRESHOLD_KM) <= 0
                        ? "VERIFIED"
                        : "REJECTED";

        GpsVerificationEntity gps =
                new GpsVerificationEntity();

        gps.setVisit(visit);
        gps.setPropertyLocation(propertyLocation);

        gps.setLatitude(
                request.getLatitude());

        gps.setLongitude(
                request.getLongitude());

        gps.setDistanceFromExpectedKm(
                distance);

        gps.setValidationStatus(
                status);

        gps.setCapturedAt(
                OffsetDateTime.now());

        gps.setValidatedAt(
                OffsetDateTime.now());

        return buildResponse(
                gpsRepository.save(gps));
    }

    @Override
    public GpsVerificationResponse getVerification(
            UUID gpsVerificationId) {

        return buildResponse(
                gpsRepository.findById(
                                gpsVerificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "GPS verification not found")));
    }

    private BigDecimal calculateDistance(
            PropertyLocationEntity location,
            BigDecimal actualLat,
            BigDecimal actualLon) {

        double lat1 =
                location.getLatitude()
                        .doubleValue();

        double lon1 =
                location.getLongitude()
                        .doubleValue();

        double lat2 =
                actualLat.doubleValue();

        double lon2 =
                actualLon.doubleValue();

        double dLat =
                Math.toRadians(lat2 - lat1);

        double dLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2)
                        * Math.sin(dLat / 2)
                        + Math.cos(
                        Math.toRadians(lat1))
                        * Math.cos(
                        Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a));

        double distance =
                EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(
                        distance)
                .setScale(
                        3,
                        RoundingMode.HALF_UP);
    }

    private GpsVerificationResponse buildResponse(
            GpsVerificationEntity gps) {

        GpsVerificationResponse response =
                new GpsVerificationResponse();

        response.setGpsVerificationId(
                gps.getGpsVerificationId());

        response.setVisitId(
                gps.getVisit()
                        .getVisitId());

        response.setPropertyLocationId(
                gps.getPropertyLocation()
                        .getPropertyLocationId());

        response.setValidationStatus(
                gps.getValidationStatus());

        response.setLatitude(
                gps.getLatitude());

        response.setLongitude(
                gps.getLongitude());

        response.setDistanceFromExpectedKm(
                gps.getDistanceFromExpectedKm());

        response.setCapturedAt(
                gps.getCapturedAt());

        response.setValidatedAt(
                gps.getValidatedAt());

        return response;
    }
}