package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_search_history")
@Getter
@Setter
public class CustomerSearchHistoryEntity extends AuditableEntity {

  @Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "customer_search_history_id")
private UUID customerSearchHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "search_type", nullable = false, length = 30)
    private String searchType;

    @Column(
            name = "search_criteria",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String searchCriteria;

    @Column(name = "result_count", nullable = false)
    private Integer resultCount;

    @Column(name = "searched_at", nullable = false)
    private OffsetDateTime searchedAt;
}