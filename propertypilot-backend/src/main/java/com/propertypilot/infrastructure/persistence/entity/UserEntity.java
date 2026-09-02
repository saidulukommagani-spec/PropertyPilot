package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.propertypilot.domain.enums.UserStatus;

@Entity
@Table(name = "users")
public class UserEntity extends AuditableEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "password_hash")
    private String passwordHash;

       @Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false)
private UserStatus status;

    @Column(name = "email_verified_at")
    private OffsetDateTime emailVerifiedAt;

    @Column(name = "mobile_verified_at")
    private OffsetDateTime mobileVerifiedAt;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

   public UserStatus getStatus() {
    return status;
}

public void setStatus(UserStatus status) {
    this.status = status;
}

public UUID getUserId() {
    return userId;
}

public void setUserId(UUID userId) {
    this.userId = userId;
}

public String getFullName() {
    return fullName;
}

public void setFullName(String fullName) {
    this.fullName = fullName;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getMobileNumber() {
    return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
}

public String getPasswordHash() {
    return passwordHash;
}

public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
}

public OffsetDateTime getEmailVerifiedAt() {
    return emailVerifiedAt;
}

public void setEmailVerifiedAt(OffsetDateTime emailVerifiedAt) {
    this.emailVerifiedAt = emailVerifiedAt;
}

public OffsetDateTime getMobileVerifiedAt() {
    return mobileVerifiedAt;
}

public void setMobileVerifiedAt(OffsetDateTime mobileVerifiedAt) {
    this.mobileVerifiedAt = mobileVerifiedAt;
}

public OffsetDateTime getLastLoginAt() {
    return lastLoginAt;
}

public void setLastLoginAt(OffsetDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
}

}