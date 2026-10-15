package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

/** Server-side Google Maps road-distance integration for estimates. */
@Service
public class GoogleMapsDistanceService {
    private static final String ROUTES_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";

    private final RestClient restClient;
    private final String apiKey;
    private final String officeAddress;
    private final String travelMode;

    @Autowired
    public GoogleMapsDistanceService(
            @Value("${propertypilot.maps.api-key:}") String apiKey,
            @Value("${propertypilot.maps.office-address:LB Nagar, Hyderabad, Telangana, India}") String officeAddress,
            @Value("${propertypilot.maps.travel-mode:DRIVE}") String travelMode) {
        this(apiKey, officeAddress, travelMode, createRestClient());
    }

    GoogleMapsDistanceService(String apiKey, String officeAddress, String travelMode, RestClient restClient) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.officeAddress = officeAddress == null ? "" : officeAddress.trim();
        this.travelMode = travelMode == null || travelMode.isBlank() ? "DRIVE" : travelMode.trim().toUpperCase();
        this.restClient = restClient;
    }

    private static RestClient createRestClient() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder().requestFactory(requestFactory).build();
    }

    public boolean isConfigured() {
        return !apiKey.isBlank() && !officeAddress.isBlank();
    }

    public BigDecimal calculateOneWayRoadDistanceKm(PropertyLocationEntity propertyLocation) {
        requireConfigured();
        if (propertyLocation == null) {
            throw new BusinessException("The property's location is unavailable. Operations will prepare the quote.");
        }
        String destinationAddress = propertyAddress(propertyLocation);
        if (destinationAddress.isBlank()) {
            throw new BusinessException("The property's address is incomplete. Update the address or contact Operations for a quote.");
        }
        try {
            JsonNode response = restClient.post()
                    .uri(ROUTES_URL)
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "routes.distanceMeters")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "origin", Map.of("address", officeAddress),
                            "destination", Map.of("address", destinationAddress),
                            "travelMode", travelMode,
                            "routingPreference", "TRAFFIC_UNAWARE",
                            "units", "METRIC",
                            "regionCode", propertyLocation.getCountryCode() == null || propertyLocation.getCountryCode().isBlank()
                                    ? "IN" : propertyLocation.getCountryCode().trim().toUpperCase()))
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode routes = response == null ? null : response.path("routes");
            if (routes == null || !routes.isArray() || routes.isEmpty()
                    || !routes.get(0).path("distanceMeters").canConvertToLong()) {
                throw new BusinessException("Google Maps could not find a road route from the LB Nagar office to this property. Operations will prepare the quote.");
            }
            return BigDecimal.valueOf(routes.get(0).path("distanceMeters").asLong())
                    .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException("Google Maps could not calculate the road distance right now. Operations will prepare the quote.");
        }
    }

    private void requireConfigured() {
        if (!isConfigured()) {
            throw new BusinessException("Google Maps pricing is not configured yet. Operations will prepare the quote.");
        }
    }

    private String propertyAddress(PropertyLocationEntity location) {
        String country = "IN".equalsIgnoreCase(location.getCountryCode()) ? "India" : location.getCountryCode();
        var addressParts = Stream.of(location.getAddressLine1(), location.getAddressLine2(),
                        location.getLocalityName(), location.getCity(), location.getDistrict(),
                        location.getState(), location.getPostalCode())
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (addressParts.isEmpty()) return "";
        return Stream.concat(addressParts.stream(), Stream.of(country))
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
