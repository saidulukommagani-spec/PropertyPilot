package com.propertypilot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentSettlementVerifierTest {
    private static final String SECRET = "test-only-payment-webhook-secret-value";
    private static final String BODY = """
            {"eventId":"evt-34","provider":"ACQUIRER","providerPaymentId":"tx-98","invoiceId":"11223344-5566-7788-9900-aabbccddeeff","amount":118.00,"currencyCode":"INR","status":"SUCCEEDED","settledAt":"2026-10-08T10:15:30Z","failureReason":null}
            """.trim();

    @Test
    void verifiesSignatureAgainstExactRawBody() {
        PaymentSettlementVerifier verifier = new PaymentSettlementVerifier(objectMapper(), SECRET);

        var event = verifier.verify(BODY, "sha256=" + sign(BODY));

        assertThat(event.eventId()).isEqualTo("evt-34");
        assertThat(event.invoiceId()).isEqualTo(UUID.fromString("11223344-5566-7788-9900-aabbccddeeff"));
        assertThat(event.amount()).isEqualByComparingTo("118.00");
    }

    @Test
    void rejectsInvalidSignature() {
        PaymentSettlementVerifier verifier = new PaymentSettlementVerifier(objectMapper(), SECRET);

        assertThatThrownBy(() -> verifier.verify(BODY, "00".repeat(32)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid payment settlement signature");
    }

    @Test
    void failsClosedWhenSecretIsNotConfigured() {
        PaymentSettlementVerifier verifier = new PaymentSettlementVerifier(objectMapper(), " ");

        assertThatThrownBy(() -> verifier.verify(BODY, sign(BODY)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payment settlement verification is not configured");
    }

    @Test
    void rejectsConfiguredWebhookSecretsShorterThan32Bytes() {
        assertThatThrownBy(() -> new PaymentSettlementVerifier(objectMapper(), "short-secret"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PAYMENT_WEBHOOK_SECRET must contain at least 32 UTF-8 bytes");
    }

    @Test
    void rejectsMissingWebhookSecretInProductionProfile() {
        assertThatThrownBy(() -> new PaymentSettlementVerifier(objectMapper(), " ", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PAYMENT_WEBHOOK_SECRET is required in the prod profile");
    }

    private static String sign(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
