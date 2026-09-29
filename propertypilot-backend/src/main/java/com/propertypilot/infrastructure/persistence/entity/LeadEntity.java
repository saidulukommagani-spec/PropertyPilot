package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.propertypilot.domain.enums.LeadStatus;

@Entity
@Table(name = "leads")
public class LeadEntity extends AuditableEntity {

  @Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "lead_id")
private UUID leadId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "assigned_agent_id")
private AgentEntity assignedAgent;
    @Column(name = "source")
    private String source;

  
@Enumerated(EnumType.STRING)
@Column(name = "status")
private LeadStatus status;

    @Column(name = "budget_min")
    private BigDecimal budgetMin;

    @Column(name = "budget_max")
    private BigDecimal budgetMax;

    @Column(name = "notes")
    private String notes;

    public UUID getLeadId() {
        return leadId;
    }

    // public void setLeadId(UUID leadId) {
    //     this.leadId = leadId;
    // }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

public LeadStatus getStatus() {
    return status;
}

public void setStatus(LeadStatus status) {
    this.status = status;
}

    public BigDecimal getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(BigDecimal budgetMin) {
        this.budgetMin = budgetMin;
    }

    public BigDecimal getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(BigDecimal budgetMax) {
        this.budgetMax = budgetMax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

@Column(name = "property_type")
private String propertyType;

@Column(name = "location")
private String location;

@Column(name = "contacted_at")
private OffsetDateTime contactedAt;

@Column(name = "converted_at")
private OffsetDateTime convertedAt;

public String getPropertyType() {
    return propertyType;
}

public void setPropertyType(
        String propertyType) {

    this.propertyType = propertyType;
}

public String getLocation() {
    return location;
}

public void setLocation(
        String location) {

    this.location = location;
}

public OffsetDateTime getContactedAt() {
    return contactedAt;
}

public void setContactedAt(
        OffsetDateTime contactedAt) {

    this.contactedAt = contactedAt;
}

public OffsetDateTime getConvertedAt() {
    return convertedAt;
}

public void setConvertedAt(
        OffsetDateTime convertedAt) {

    this.convertedAt = convertedAt;
}
public AgentEntity getAssignedAgent() {
    return assignedAgent;
}

public void setAssignedAgent(
        AgentEntity assignedAgent) {

    this.assignedAgent = assignedAgent;
}

}