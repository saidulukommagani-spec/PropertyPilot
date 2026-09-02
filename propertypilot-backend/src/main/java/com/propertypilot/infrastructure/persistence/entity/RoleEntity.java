package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;
import com.propertypilot.domain.enums.RoleStatus;

@Entity
@Table(name = "roles")
public class RoleEntity extends AuditableEntity {

    @Id
    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    @Column(name = "role_name", nullable = false)
    private String roleName;


   @Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false)
private RoleStatus status;
    // getters/setters
    public RoleStatus getStatus() {
    return status;
}

public void setStatus(RoleStatus status) {
    this.status = status;
}
public UUID getRoleId() {
    return roleId;
}

public void setRoleId(UUID roleId) {
    this.roleId = roleId;
}

public String getRoleCode() {
    return roleCode;
}

public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
}

public String getRoleName() {
    return roleName;
}

public void setRoleName(String roleName) {
    this.roleName = roleName;
}
}