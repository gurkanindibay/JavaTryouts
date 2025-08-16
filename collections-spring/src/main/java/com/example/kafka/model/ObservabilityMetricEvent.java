package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kafka event model for observability metrics
 */
public class ObservabilityMetricEvent {
    
    @JsonProperty("metricName")
    private String metricName;
    
    @JsonProperty("metricValue")
    private Long metricValue;
    
    @JsonProperty("metricType")
    private String metricType;
    
    @JsonProperty("operation")
    private String operation; // INCREMENT, SET, etc.
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    @JsonProperty("eventId")
    private String eventId;
    
    // Default constructor
    public ObservabilityMetricEvent() {
        this.timestamp = System.currentTimeMillis();
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    // Constructor for increment operations
    public ObservabilityMetricEvent(String metricName, Long metricValue, String operation) {
        this();
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricType = "COUNTER";
        this.operation = operation;
        this.description = "Persistent observability counter";
    }
    
    // Full constructor
    public ObservabilityMetricEvent(String metricName, Long metricValue, String metricType, String operation, String description) {
        this();
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricType = metricType;
        this.operation = operation;
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
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    @Override
    public String toString() {
        return "ObservabilityMetricEvent{" +
                "metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", metricType='" + metricType + '\'' +
                ", operation='" + operation + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", eventId='" + eventId + '\'' +
                '}';
    }
}