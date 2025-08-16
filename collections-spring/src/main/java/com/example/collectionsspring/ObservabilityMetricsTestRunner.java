package com.example.collectionsspring;

import com.example.kafka.model.ObservabilityMetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Test component to send observability metric events to Kafka for testing persistence.
 * This runs once at startup to verify the observability metrics consumer is working.
 */
//@Component  // Temporarily disabled to avoid startup issues
public class ObservabilityMetricsTestRunner implements CommandLineRunner {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ObservabilityMetricsTestRunner(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Wait a bit for Kafka listeners to be ready
        Thread.sleep(8000);
        
        System.out.println("=== STARTING OBSERVABILITY METRICS TEST ===");
        
        // Send test observability metric events
        sendTestObservabilityEvents();
        
        System.out.println("=== OBSERVABILITY METRICS TEST COMPLETED ===");
    }

    private void sendTestObservabilityEvents() {
        try {
            // Test increment events
            sendObservabilityEvent("test.api.calls.total", "COUNTER", "INCREMENT", 1L);
            sendObservabilityEvent("test.errors.total", "COUNTER", "INCREMENT", 1L);
            sendObservabilityEvent("library.books.duplicate.attempts.total", "COUNTER", "INCREMENT", 1L);
            
            // Test set events
            sendObservabilityEvent("test.current.users", "GAUGE", "SET", 42L);
            
            // Test reset event
            sendObservabilityEvent("test.reset.counter", "COUNTER", "RESET", 0L);
            
            System.out.println("Sent 5 test observability metric events to Kafka");
            
        } catch (Exception e) {
            System.err.println("Error sending test observability events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendObservabilityEvent(String metricName, String metricType, String operation, Long value) {
        try {
            ObservabilityMetricEvent event = new ObservabilityMetricEvent(metricName, value, metricType, operation, "Test event from ObservabilityMetricsTestRunner");
            
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("observability-metrics", metricName, eventJson);
            
            System.out.println("Sent: " + operation + " " + metricName + " = " + value + " (" + metricType + ")");
            
        } catch (Exception e) {
            System.err.println("Error sending observability event for " + metricName + ": " + e.getMessage());
        }
    }
}