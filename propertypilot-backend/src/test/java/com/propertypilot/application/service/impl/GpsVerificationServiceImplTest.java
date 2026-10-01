package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;
import com.propertypilot.infrastructure.persistence.entity.GpsVerificationEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.GpsVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyLocationRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpsVerificationServiceImplTest {

    @Mock
    private GpsVerificationRepository gpsRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PropertyLocationRepository propertyLocationRepository;

    @InjectMocks
    private GpsVerificationServiceImpl service;

    @Test
    void createVerification_verified() {

        UUID visitId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CreateGpsVerificationRequest request =
                new CreateGpsVerificationRequest();

        request.setVisitId(visitId);
        request.setLatitude(
                new BigDecimal("17.385044"));
        request.setLongitude(
                new BigDecimal("78.486671"));

        Property property = new Property();
        property.setPropertyId(propertyId);

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();
        serviceRequest.setProperty(property);

        VisitEntity visit = new VisitEntity();
        visit.setVisitId(visitId);
        visit.setServiceRequest(serviceRequest);

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        location.setPropertyLocationId(
                UUID.randomUUID());

        location.setProperty(property);

        location.setLatitude(
                new BigDecimal("17.385044"));

        location.setLongitude(
                new BigDecimal("78.486671"));

        when(visitRepository.findById(visitId))
                .thenReturn(Optional.of(visit));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(gpsRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        GpsVerificationResponse response =
                service.createVerification(request);

        assertThat(response)
                .isNotNull();

        assertThat(response.getValidationStatus())
                .isEqualTo("VERIFIED");

        assertThat(response.getVisitId())
                .isEqualTo(visitId);

        assertThat(response.getPropertyLocationId())
                .isEqualTo(
                        location.getPropertyLocationId());
    }

    @Test
    void createVerification_rejected() {

        UUID visitId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CreateGpsVerificationRequest request =
                new CreateGpsVerificationRequest();

        request.setVisitId(visitId);

        request.setLatitude(
                new BigDecimal("18.385044"));

        request.setLongitude(
                new BigDecimal("79.486671"));

        Property property = new Property();
        property.setPropertyId(propertyId);

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setProperty(property);

        VisitEntity visit = new VisitEntity();
        visit.setVisitId(visitId);
        visit.setServiceRequest(serviceRequest);

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        location.setPropertyLocationId(
                UUID.randomUUID());

        location.setProperty(property);

        location.setLatitude(
                new BigDecimal("17.385044"));

        location.setLongitude(
                new BigDecimal("78.486671"));

        when(visitRepository.findById(visitId))
                .thenReturn(Optional.of(visit));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(gpsRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        GpsVerificationResponse response =
                service.createVerification(request);

        assertThat(response)
                .isNotNull();

        assertThat(response.getValidationStatus())
                .isEqualTo("REJECTED");
    }

    @Test
    void createVerification_visitNotFound() {

        UUID visitId = UUID.randomUUID();

        CreateGpsVerificationRequest request =
                new CreateGpsVerificationRequest();

        request.setVisitId(visitId);

        when(visitRepository.findById(visitId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createVerification(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage("Visit not found");
    }

    @Test
    void createVerification_propertyLocationNotFound() {

        UUID visitId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CreateGpsVerificationRequest request =
                new CreateGpsVerificationRequest();

        request.setVisitId(visitId);

        Property property = new Property();
        property.setPropertyId(propertyId);

        ServiceRequestEntity serviceRequest =
                new ServiceRequestEntity();

        serviceRequest.setProperty(property);

        VisitEntity visit = new VisitEntity();
        visit.setVisitId(visitId);
        visit.setServiceRequest(serviceRequest);

        when(visitRepository.findById(visitId))
                .thenReturn(Optional.of(visit));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createVerification(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Property location not found");
    }

    @Test
    void getVerification_success() {

        UUID gpsId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        VisitEntity visit = new VisitEntity();
        visit.setVisitId(visitId);

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        location.setPropertyLocationId(locationId);

        GpsVerificationEntity gps =
                new GpsVerificationEntity();

        gps.setGpsVerificationId(gpsId);
        gps.setVisit(visit);
        gps.setPropertyLocation(location);
        gps.setValidationStatus("VERIFIED");

        gps.setLatitude(
                new BigDecimal("17.385044"));

        gps.setLongitude(
                new BigDecimal("78.486671"));

        gps.setDistanceFromExpectedKm(
                BigDecimal.ZERO);

        gps.setCapturedAt(
                OffsetDateTime.now());

        gps.setValidatedAt(
                OffsetDateTime.now());

        when(gpsRepository.findById(gpsId))
                .thenReturn(Optional.of(gps));

        GpsVerificationResponse response =
                service.getVerification(gpsId);

        assertThat(response)
                .isNotNull();

        assertThat(response.getVisitId())
                .isEqualTo(visitId);

        assertThat(response.getPropertyLocationId())
                .isEqualTo(locationId);

        assertThat(response.getValidationStatus())
                .isEqualTo("VERIFIED");
    }

    @Test
    void getVerification_notFound() {

        UUID gpsId = UUID.randomUUID();

        when(gpsRepository.findById(gpsId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getVerification(gpsId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "GPS verification not found");
    }
}