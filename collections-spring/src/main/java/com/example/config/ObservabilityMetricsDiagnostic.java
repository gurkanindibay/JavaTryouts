package com.example.config;

import com.example.config.entity.ObservabilityMetricEntity;
import com.example.config.repository.ObservabilityMetricRepository;
import com.example.kafka.model.ObservabilityMetricEvent;
import com.example.kafka.producer.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Diagnostic component for observability metrics
 * Helps debug persistence issues with observability metrics
 */
@Component
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "with-kafka")
public class ObservabilityMetricsDiagnostic {
    
    private static final Logger logger = LoggerFactory.getLogger(ObservabilityMetricsDiagnostic.class);
    
    @Autowired
    private ObservabilityMetricRepository observabilityMetricRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @PostConstruct
    public void runDiagnostics() {
        logger.info("=== Observability Metrics Diagnostic ===");
        
        try {
            // Check if repository is working
            long count = observabilityMetricRepository.count();
            logger.info("Total observability metrics in database: {}", count);
            
            // List all existing metrics
            List<ObservabilityMetricEntity> allMetrics = observabilityMetricRepository.findAll();
            if (allMetrics.isEmpty()) {
                logger.info("No observability metrics found in database");
                logger.info("Creating test metrics to verify persistence...");
                createTestMetrics();
            } else {
                logger.info("Found {} observability metrics:", allMetrics.size());
                allMetrics.forEach(metric -> 
                    logger.info("  - {} = {} (type: {}, updated: {})", 
                        metric.getMetricName(), 
                        metric.getMetricValue(), 
                        metric.getMetricType(),
                        metric.getLastUpdated())
                );
            }
            
        } catch (Exception e) {
            logger.error("Diagnostic failed: {}", e.getMessage(), e);
        }
        
        logger.info("=== End Diagnostic ===");
    }
    
    /**
     * Create test metrics to verify the system is working
     */
    @Transactional
    public void createTestMetrics() {
        try {
            logger.info("Creating test observability metrics via Kafka...");
            
            // Send test events to Kafka
            ObservabilityMetricEvent event1 = new ObservabilityMetricEvent(
                "library.test.counter.total", 1L, "COUNTER", "INCREMENT", "Test counter metric"
            );
            kafkaProducerService.sendObservabilityMetricEvent(event1);
            
            ObservabilityMetricEvent event2 = new ObservabilityMetricEvent(
                "library.test.gauge.current", 50L, "GAUGE", "SET", "Test gauge metric"
            );
            kafkaProducerService.sendObservabilityMetricEvent(event2);
            
            logger.info("Test observability metric events sent to Kafka");
            logger.info("Check logs for consumer processing and database persistence");
            
        } catch (Exception e) {
            logger.error("Failed to create test metrics: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Manual verification method for direct database persistence
     */
    @Transactional
    public void testDirectPersistence() {
        try {
            logger.info("Testing direct database persistence...");
            
            ObservabilityMetricEntity testMetric = new ObservabilityMetricEntity(
                "library.direct.test.metric", 
                42L, 
                "COUNTER", 
                "Direct persistence test"
            );
            
            ObservabilityMetricEntity saved = observabilityMetricRepository.save(testMetric);
            logger.info("Direct persistence successful: {} = {} (version: {})", 
                saved.getMetricName(), saved.getMetricValue(), saved.getVersion());
            
            // Verify it can be read back
            var found = observabilityMetricRepository.findByMetricName(testMetric.getMetricName());
            if (found.isPresent()) {
                logger.info("Direct retrieval successful: {}", found.get());
            } else {
                logger.error("Direct retrieval failed - metric not found");
            }
            
        } catch (Exception e) {
            logger.error("Direct persistence test failed: {}", e.getMessage(), e);
        }
    }
}