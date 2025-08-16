package com.example.config;

import com.example.config.entity.ObservabilityMetricEntity;
import com.example.config.repository.ObservabilityMetricRepository;
import com.example.kafka.model.ObservabilityMetricEvent;
import com.example.kafka.producer.KafkaProducerService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for persisting meaningful observability metrics that can't be derived from business data.
 * Now uses Kafka for async processing and JPA for persistence.
 * Focus on operational metrics like errors, failures, conflicts, etc.
 */
@Service
@Transactional
public class ObservabilityMetricsService {
    
    private static final Logger logger = LoggerFactory.getLogger(ObservabilityMetricsService.class);
    
    @Autowired
    private ObservabilityMetricRepository observabilityMetricRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    // Persistent counters for meaningful observability metrics
    private final Map<String, AtomicLong> persistentCounters = new ConcurrentHashMap<>();
    
    // Micrometer counters for Prometheus exposure
    private final Map<String, Counter> micrometerCounters = new ConcurrentHashMap<>();
    
    // Define which metrics should be persisted - only meaningful observability metrics
    private static final Set<String> PERSISTENT_OBSERVABILITY_METRICS = Set.of(
        "library.books.duplicate.attempts.total",
        "library.errors.total", 
        "library.api.failures.total",
        "library.validation.failures.total",
        "library.concurrent.access.conflicts.total",
        "library.cache.misses.total",
        "library.performance.slow.operations.total"
    );
    
    @PostConstruct
    public void initializeObservabilityMetrics() {
        logger.info("Initializing persistent observability metrics with Kafka and JPA...");
        
        // Load persisted observability metrics from database using JPA
        loadPersistedObservabilityMetrics();
        
        logger.info("Persistent observability metrics initialized successfully");
    }
    
    private void loadPersistedObservabilityMetrics() {
        try {
            // Use JPA to load all counter metrics
            observabilityMetricRepository.findAllCounters().forEach(metric -> {
                // Only restore metrics that are in our observability metrics set
                if (PERSISTENT_OBSERVABILITY_METRICS.contains(metric.getMetricName())) {
                    AtomicLong counter = new AtomicLong(metric.getMetricValue());
                    persistentCounters.put(metric.getMetricName(), counter);
                    
                    // Create a Micrometer counter and set it to the persisted value
                    String micrometerName = metric.getMetricName().replace(".", "_");
                    Counter micrometerCounter = Counter.builder(micrometerName)
                        .description(getMetricDescription(metric.getMetricName()))
                        .tag("application", "collections-kafka-challenge")
                        .register(meterRegistry);
                    
                    // Increment the counter to match the persisted value
                    micrometerCounter.increment(metric.getMetricValue());
                    micrometerCounters.put(metric.getMetricName(), micrometerCounter);
                    
                    logger.info("Restored observability counter {} with value {} and registered with Micrometer", 
                               metric.getMetricName(), metric.getMetricValue());
                }
            });
            
            logger.info("Loaded {} persistent observability counters and registered with Micrometer", persistentCounters.size());
        } catch (Exception e) {
            logger.error("Failed to load persisted observability metrics", e);
        }
    }
    
    /**
     * Get description for a metric name.
     */
    private String getMetricDescription(String metricName) {
        return switch (metricName) {
            case "library.books.duplicate.attempts.total" -> "Total number of duplicate book addition attempts";
            case "library.api.failures.total" -> "Total number of API operation failures";
            case "library.validation.failures.total" -> "Total number of validation failures";
            case "library.concurrent.access.conflicts.total" -> "Total number of concurrent access conflicts";
            case "library.cache.misses.total" -> "Total number of cache misses";
            case "library.performance.slow.operations.total" -> "Total number of slow operations";
            case "library.errors.total" -> "Total number of general errors";
            default -> "Observability metric";
        };
    }
    
    /**
     * Increment a persistent observability counter.
     * Now sends event to Kafka for async processing.
     * Only increments if the metric is in the allowed observability metrics list.
     */
    public void incrementObservabilityCounter(String metricName) {
        if (PERSISTENT_OBSERVABILITY_METRICS.contains(metricName)) {
            AtomicLong counter = persistentCounters.computeIfAbsent(metricName, k -> {
                AtomicLong newCounter = new AtomicLong(0);
                // Register new Micrometer counter if it doesn't exist
                String micrometerName = metricName.replace(".", "_");
                Counter micrometerCounter = Counter.builder(micrometerName)
                    .description(getMetricDescription(metricName))
                    .tag("application", "collections-kafka-challenge")
                    .register(meterRegistry);
                micrometerCounters.put(metricName, micrometerCounter);
                return newCounter;
            });
            
            long newValue = counter.incrementAndGet();
            
            // Also increment the Micrometer counter
            Counter micrometerCounter = micrometerCounters.get(metricName);
            if (micrometerCounter != null) {
                micrometerCounter.increment();
            }
            
            // Send event to Kafka for async persistence
            ObservabilityMetricEvent event = new ObservabilityMetricEvent(metricName, newValue, "INCREMENT");
            kafkaProducerService.sendObservabilityMetricEvent(event);
            
            logger.debug("Incremented observability counter {} to {} and sent to Kafka", metricName, newValue);
        }
    }
    
    /**
     * Get current value of a persistent observability counter.
     */
    public long getObservabilityCounterValue(String metricName) {
        return persistentCounters.getOrDefault(metricName, new AtomicLong(0)).get();
    }
    
    /**
     * Record duplicate book attempt - this is a meaningful observability metric
     */
    public void recordDuplicateBookAttempt() {
        incrementObservabilityCounter("library.books.duplicate.attempts.total");
    }
    
    /**
     * Record API failure - meaningful for observability
     */
    public void recordApiFailure(String operation) {
        incrementObservabilityCounter("library.api.failures.total");
    }
    
    /**
     * Record validation failure - meaningful for observability
     */
    public void recordValidationFailure(String validationType) {
        incrementObservabilityCounter("library.validation.failures.total");
    }
    
    /**
     * Record concurrent access conflict - meaningful for observability
     */
    public void recordConcurrentAccessConflict() {
        incrementObservabilityCounter("library.concurrent.access.conflicts.total");
    }
}