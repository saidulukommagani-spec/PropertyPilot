package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity extends AuditableEntity {

    @Id
    @Column(name = "user_role_id")
    private UUID userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @Column(name = "effective_from")
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    public UUID getUserRoleId() {
    return userRoleId;
}

public void setUserRoleId(UUID userRoleId) {
    this.userRoleId = userRoleId;
}

public UserEntity getUser() {
    return user;
}

public void setUser(UserEntity user) {
    this.user = user;
}

public RoleEntity getRole() {
    return role;
}

public void setRole(RoleEntity role) {
    this.role = role;
}

public OffsetDateTime getEffectiveFrom() {
    return effectiveFrom;
}

public void setEffectiveFrom(OffsetDateTime effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
}

public OffsetDateTime getEffectiveTo() {
    return effectiveTo;
}

public void setEffectiveTo(OffsetDateTime effectiveTo) {
    this.effectiveTo = effectiveTo;
}
}