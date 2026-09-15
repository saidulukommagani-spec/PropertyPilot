package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "coverage_zones")
public class CoverageZoneEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coverage_zone_id")
    private UUID coverageZoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private ClusterEntity cluster;

    @Column(name = "zone_name", nullable = false, length = 120)
    private String zoneName;

    @Column(name = "zone_type", nullable = false, length = 50)
    private String zoneType;

    @Column(name = "polygon_data", columnDefinition = "jsonb")
    private String polygonData;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getCoverageZoneId() {
        return coverageZoneId;
    }

    public void setCoverageZoneId(UUID coverageZoneId) {
        this.coverageZoneId = coverageZoneId;
    }

    public ClusterEntity getCluster() {
        return cluster;
    }

    public void setCluster(ClusterEntity cluster) {
        this.cluster = cluster;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public String getPolygonData() {
        return polygonData;
    }

    public void setPolygonData(String polygonData) {
        this.polygonData = polygonData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}