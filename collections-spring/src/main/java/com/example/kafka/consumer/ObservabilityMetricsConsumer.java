package com.example.kafka.consumer;

import com.example.config.entity.ObservabilityMetricEntity;
import com.example.config.repository.ObservabilityMetricRepository;
import com.example.kafka.model.ObservabilityMetricEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for async processing of observability metrics
 */
@Service
@Transactional
public class ObservabilityMetricsConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ObservabilityMetricsConsumer.class);
    
    @Autowired
    private ObservabilityMetricRepository observabilityMetricRepository;
    
    /**
     * Consumer for observability metric events
     * Improved error handling and transaction management
     */
    @KafkaListener(
        topics = "observability-metrics", 
        groupId = "observability-metrics-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeObservabilityMetricEvent(
            @Payload ObservabilityMetricEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Consuming observability metric event from partition {}, offset {}: {}", 
                partition, offset, event);
        
        try {
            // Validate the event before processing
            if (!isValidEvent(event)) {
                logger.warn("Invalid observability metric event received: {}", event);
                acknowledgment.acknowledge(); // Skip invalid events
                return;
            }
            
            // Check database connectivity before processing
            if (!isDatabaseHealthy()) {
                logger.error("Database is not healthy, cannot process observability metric event");
                // Don't acknowledge - this will cause the event to be retried
                throw new RuntimeException("Database connectivity issue");
            }
            
            processObservabilityMetricEvent(event);
            acknowledgment.acknowledge();
            logger.debug("Successfully processed observability metric event: {}", event.getMetricName());
        } catch (Exception e) {
            logger.error("Error processing observability metric event: {} - Event: {}", e.getMessage(), event, e);
            // Don't acknowledge - this will cause the event to be retried
            // In production, you might want to implement retry logic or send to DLQ after max retries
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }
    
    /**
     * Validate the incoming observability metric event
     */
    private boolean isValidEvent(ObservabilityMetricEvent event) {
        if (event == null) {
            return false;
        }
        
        if (event.getMetricName() == null || event.getMetricName().trim().isEmpty()) {
            logger.warn("Event has null or empty metric name");
            return false;
        }
        
        if (event.getOperation() == null || event.getOperation().trim().isEmpty()) {
            logger.warn("Event has null or empty operation");
            return false;
        }
        
        if (event.getMetricValue() == null) {
            logger.warn("Event has null metric value");
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if database is healthy and accessible
     */
    private boolean isDatabaseHealthy() {
        try {
            // Simple health check - try to check if repository exists
            observabilityMetricRepository.count();
            return true;
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Process the observability metric event and persist using JPA
     */
    private void processObservabilityMetricEvent(ObservabilityMetricEvent event) {
        switch (event.getOperation().toUpperCase()) {
            case "INCREMENT":
                incrementMetric(event);
                break;
            case "SET":
                setMetric(event);
                break;
            case "RESET":
                resetMetric(event);
                break;
            default:
                logger.warn("Unknown metric operation: {}", event.getOperation());
        }
    }
    
    /**
     * Increment metric value using atomic database operation
     * This version uses database-level atomic increment to prevent race conditions
     */
    private void incrementMetric(ObservabilityMetricEvent event) {
        try {
            // Try atomic increment first
            int updatedRows = observabilityMetricRepository.incrementMetricValue(event.getMetricName());
            
            if (updatedRows > 0) {
                logger.debug("Atomically incremented metric {}", event.getMetricName());
            } else {
                // Metric doesn't exist, create it
                logger.debug("Metric {} doesn't exist, creating with value 1", event.getMetricName());
                ObservabilityMetricEntity newMetric = new ObservabilityMetricEntity(
                        event.getMetricName(),
                        1L,
                        event.getMetricType(),
                        event.getDescription()
                );
                newMetric.setLastUpdated(java.time.LocalDateTime.now());
                
                try {
                    ObservabilityMetricEntity savedMetric = observabilityMetricRepository.save(newMetric);
                    logger.debug("Created new metric {} with initial value 1 (version: {})", 
                        event.getMetricName(), savedMetric.getVersion());
                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                    // Another thread created the metric, try atomic increment again
                    logger.debug("Concurrent creation detected for metric {}, retrying increment", event.getMetricName());
                    int retryResult = observabilityMetricRepository.incrementMetricValue(event.getMetricName());
                    if (retryResult > 0) {
                        logger.debug("Retry successful for metric {}", event.getMetricName());
                    } else {
                        throw new RuntimeException("Failed to increment metric after retry: " + event.getMetricName());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to increment metric {}: {}", event.getMetricName(), e.getMessage(), e);
            throw new RuntimeException("Failed to persist metric increment", e);
        }
    }
    
    /**
     * Set metric to specific value using atomic database operation
     */
    private void setMetric(ObservabilityMetricEvent event) {
        try {
            // Try atomic set first
            int updatedRows = observabilityMetricRepository.setMetricValue(event.getMetricName(), event.getMetricValue());
            
            if (updatedRows > 0) {
                logger.debug("Atomically set metric {} to value {}", event.getMetricName(), event.getMetricValue());
            } else {
                // Metric doesn't exist, create it
                logger.debug("Metric {} doesn't exist, creating with value {}", event.getMetricName(), event.getMetricValue());
                ObservabilityMetricEntity newMetric = new ObservabilityMetricEntity(
                        event.getMetricName(),
                        event.getMetricValue(),
                        event.getMetricType(),
                        event.getDescription()
                );
                newMetric.setLastUpdated(java.time.LocalDateTime.now());
                
                ObservabilityMetricEntity savedMetric = observabilityMetricRepository.save(newMetric);
                logger.debug("Created new metric {} with value {} (version: {})", 
                    event.getMetricName(), event.getMetricValue(), savedMetric.getVersion());
            }
        } catch (Exception e) {
            logger.error("Failed to set metric {} to value {}: {}", 
                event.getMetricName(), event.getMetricValue(), e.getMessage(), e);
            throw new RuntimeException("Failed to persist metric set operation", e);
        }
    }
    
    /**
     * Reset metric to zero using atomic database operation
     */
    private void resetMetric(ObservabilityMetricEvent event) {
        try {
            // Try atomic reset
            int updatedRows = observabilityMetricRepository.resetMetricValue(event.getMetricName());
            
            if (updatedRows > 0) {
                logger.debug("Atomically reset metric {} to 0", event.getMetricName());
            } else {
                logger.debug("Metric {} does not exist, skipping reset", event.getMetricName());
            }
        } catch (Exception e) {
            logger.error("Failed to reset metric {}: {}", event.getMetricName(), e.getMessage(), e);
            throw new RuntimeException("Failed to persist metric reset operation", e);
        }
    }
}