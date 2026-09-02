package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;
import com.propertypilot.domain.enums.LeadStatus;
@Entity
@Table(name = "leads")
public class LeadEntity extends AuditableEntity {

    @Id
    @Column(name = "lead_id")
    private UUID leadId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

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

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }

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
}