package com.propertypilot.application.service.impl;

import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GoogleMapsDistanceServiceTest {
    private static final String ROUTES_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
    private static final String TEST_KEY = "unit-test-key";
    private static final String OFFICE = "LB Nagar, Hyderabad, Telangana, India";

    @Test
    void requestsRoadDistanceFromConfiguredOfficeToSavedPropertyAddressAndConvertsMetersToKm() {
        var fixture = fixture(TEST_KEY);
        fixture.server().expect(requestTo(ROUTES_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Goog-Api-Key", TEST_KEY))
                .andExpect(header("X-Goog-FieldMask", "routes.distanceMeters"))
                .andExpect(content().json("""
                        {
                          "origin": {"address": "LB Nagar, Hyderabad, Telangana, India"},
                          "destination": {"address": "Plot 5, Vanasthalipuram, Hyderabad, Telangana, 500070, India"},
                          "travelMode": "DRIVE",
                          "routingPreference": "TRAFFIC_UNAWARE",
                          "units": "METRIC",
                          "regionCode": "IN"
                        }
                        """))
                .andRespond(withSuccess("""
                        {"routes":[{"distanceMeters":12345}]}
                        """, MediaType.APPLICATION_JSON));

        BigDecimal distance = fixture.service().calculateOneWayRoadDistanceKm(propertyLocation());

        assertThat(distance).isEqualByComparingTo("12.345");
        fixture.server().verify();
    }

    @Test
    void missingKeyFailsBeforeCallingGoogleAndDoesNotExposeCredentials() {
        var fixture = fixture("");

        assertThatThrownBy(() -> fixture.service().calculateOneWayRoadDistanceKm(propertyLocation()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Google Maps pricing is not configured")
                .hasMessageNotContaining(TEST_KEY);

        fixture.server().verify();
    }

    @Test
    void countryOnlyPropertyIsRejectedInsteadOfRoutingToAnAmbiguousDestination() {
        var fixture = fixture(TEST_KEY);
        PropertyLocationEntity location = new PropertyLocationEntity();
        location.setCountryCode("IN");

        assertThatThrownBy(() -> fixture.service().calculateOneWayRoadDistanceKm(location))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("address is incomplete");

        fixture.server().verify();
    }

    @Test
    void unavailableRouteReturnsAnActionableBusinessError() {
        var fixture = fixture(TEST_KEY);
        fixture.server().expect(requestTo(ROUTES_URL))
                .andRespond(withSuccess("{\"routes\":[]}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> fixture.service().calculateOneWayRoadDistanceKm(propertyLocation()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("could not find a road route")
                .hasMessageNotContaining(TEST_KEY);

        fixture.server().verify();
    }

    @Test
    void malformedRouteResponseDoesNotProduceAZeroDistanceEstimate() {
        var fixture = fixture(TEST_KEY);
        fixture.server().expect(requestTo(ROUTES_URL))
                .andRespond(withSuccess("{\"routes\":[{\"distanceMeters\":\"unknown\"}]}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> fixture.service().calculateOneWayRoadDistanceKm(propertyLocation()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("could not find a road route");

        fixture.server().verify();
    }

    @Test
    void providerQuotaOrAuthenticationErrorIsSanitizedForTheCustomer() {
        var fixture = fixture(TEST_KEY);
        fixture.server().expect(requestTo(ROUTES_URL))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS).body("private provider diagnostic"));

        assertThatThrownBy(() -> fixture.service().calculateOneWayRoadDistanceKm(propertyLocation()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("could not calculate the road distance")
                .hasMessageNotContaining(TEST_KEY)
                .hasMessageNotContaining("private provider diagnostic");

        fixture.server().verify();
    }

    private static Fixture fixture(String key) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        return new Fixture(new GoogleMapsDistanceService(key, OFFICE, "DRIVE", builder.build()), server);
    }

    private static PropertyLocationEntity propertyLocation() {
        PropertyLocationEntity location = new PropertyLocationEntity();
        location.setAddressLine1("Plot 5");
        location.setLocalityName("Vanasthalipuram");
        location.setCity("Hyderabad");
        location.setState("Telangana");
        location.setPostalCode("500070");
        location.setCountryCode("IN");
        return location;
    }

    private record Fixture(GoogleMapsDistanceService service, MockRestServiceServer server) {}
}
