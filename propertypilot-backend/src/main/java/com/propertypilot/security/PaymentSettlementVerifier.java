package com.propertypilot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.PaymentSettlementEvent;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class PaymentSettlementVerifier {
    private final ObjectMapper objectMapper;
    private final String secret;

    @Autowired
    public PaymentSettlementVerifier(ObjectMapper objectMapper,
            @Value("${propertypilot.payment.webhook-secret:}") String secret,
            Environment environment) {
        this(objectMapper, secret, environment.acceptsProfiles(Profiles.of("prod")));
    }

    PaymentSettlementVerifier(ObjectMapper objectMapper, String secret) {
        this(objectMapper, secret, false);
    }

    PaymentSettlementVerifier(ObjectMapper objectMapper, String secret, boolean production) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        if (production && (secret == null || secret.isBlank())) {
            throw new IllegalStateException("PAYMENT_WEBHOOK_SECRET is required in the prod profile");
        }
        if (secret != null && !secret.isBlank() && secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("PAYMENT_WEBHOOK_SECRET must contain at least 32 UTF-8 bytes");
        }
    }

    public PaymentSettlementEvent verify(String rawBody, String suppliedSignature) {
        if (secret == null || secret.isBlank()) {
            throw new BusinessException("Payment settlement verification is not configured");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String normalized = suppliedSignature == null ? "" : suppliedSignature.trim();
            if (normalized.startsWith("sha256=")) normalized = normalized.substring(7);
            byte[] supplied = HexFormat.of().parseHex(normalized);
            if (!MessageDigest.isEqual(expected, supplied)) {
                throw new BusinessException("Invalid payment settlement signature");
            }
            return objectMapper.readValue(rawBody, PaymentSettlementEvent.class);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("Invalid payment settlement event");
        }
    }
}
