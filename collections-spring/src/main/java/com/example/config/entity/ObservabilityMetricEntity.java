package com.example.config.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for persisting observability metrics
 */
@Entity
@Table(name = "observability_metrics")
public class ObservabilityMetricEntity {
    
    @Id
    @Column(name = "metric_name", nullable = false, length = 255)
    private String metricName;
    
    @Column(name = "metric_value", nullable = false)
    private Long metricValue;
    
    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Version
    private Long version;
    
    // Default constructor
    public ObservabilityMetricEntity() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Constructor
    public ObservabilityMetricEntity(String metricName, Long metricValue, String metricType, String description) {
        this();
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricType = metricType;
        this.description = description;
    }
    
    // Getters and Setters
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    
    public Long getMetricValue() {
        return metricValue;
    }
    
    public void setMetricValue(Long metricValue) {
        this.metricValue = metricValue;
    }
    
    public String getMetricType() {
        return metricType;
    }
    
    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ObservabilityMetricEntity{" +
                "metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", metricType='" + metricType + '\'' +
                ", description='" + description + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", version=" + version +
                '}';
    }
}