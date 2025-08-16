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

import java.util.Optional;

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
            processObservabilityMetricEvent(event);
            acknowledgment.acknowledge();
            logger.debug("Successfully processed observability metric event: {}", event.getMetricName());
        } catch (Exception e) {
            logger.error("Error processing observability metric event: {}", e.getMessage(), e);
            // In production, you might want to implement retry logic or send to DLQ
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
     * Increment metric value using JPA
     * For INCREMENT operation, we increment the database value by 1, not set it to the event value
     */
    private void incrementMetric(ObservabilityMetricEvent event) {
        Optional<ObservabilityMetricEntity> existingMetric = 
                observabilityMetricRepository.findByMetricName(event.getMetricName());
        
        if (existingMetric.isPresent()) {
            // Increment existing metric by 1
            ObservabilityMetricEntity metric = existingMetric.get();
            long newValue = metric.getMetricValue() + 1;
            metric.setMetricValue(newValue);
            observabilityMetricRepository.save(metric);
            logger.debug("Incremented metric {} from {} to {}", event.getMetricName(), metric.getMetricValue() - 1, newValue);
        } else {
            // Create new metric starting at 1
            ObservabilityMetricEntity newMetric = new ObservabilityMetricEntity(
                    event.getMetricName(),
                    1L,
                    event.getMetricType(),
                    event.getDescription()
            );
            observabilityMetricRepository.save(newMetric);
            logger.debug("Created new metric {} with initial value 1", event.getMetricName());
        }
    }
    
    /**
     * Set metric to specific value using JPA
     */
    private void setMetric(ObservabilityMetricEvent event) {
        ObservabilityMetricEntity metric = observabilityMetricRepository
                .findByMetricName(event.getMetricName())
                .orElse(new ObservabilityMetricEntity(
                        event.getMetricName(),
                        event.getMetricValue(),
                        event.getMetricType(),
                        event.getDescription()
                ));
        
        metric.setMetricValue(event.getMetricValue());
        observabilityMetricRepository.save(metric);
        logger.debug("Set metric {} to value {}", event.getMetricName(), event.getMetricValue());
    }
    
    /**
     * Reset metric to zero using JPA
     */
    private void resetMetric(ObservabilityMetricEvent event) {
        Optional<ObservabilityMetricEntity> existingMetric = 
                observabilityMetricRepository.findByMetricName(event.getMetricName());
        
        if (existingMetric.isPresent()) {
            ObservabilityMetricEntity metric = existingMetric.get();
            metric.setMetricValue(0L);
            observabilityMetricRepository.save(metric);
            logger.debug("Reset metric {} to 0", event.getMetricName());
        } else {
            logger.debug("Metric {} does not exist, skipping reset", event.getMetricName());
        }
    }
}