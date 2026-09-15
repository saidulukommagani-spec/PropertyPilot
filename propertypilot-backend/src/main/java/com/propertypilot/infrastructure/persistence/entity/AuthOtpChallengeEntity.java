package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "auth_otp_challenges")
public class AuthOtpChallengeEntity extends AuditableEntity {

    @Id
    @Column(name = "otp_challenge_id")
    private UUID otpChallengeId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "identifier", nullable = false, length = 255)
    private String identifier;

    @Column(name = "purpose", nullable = false, length = 50)
    private String purpose;

    @Column(name = "delivery_channel", nullable = false, length = 30)
    private String deliveryChannel;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;
}